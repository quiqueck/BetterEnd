package org.betterx.betterend.world.structures.piece;

import net.minecraft.world.level.levelgen.structure.StructurePiece;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Pure-logic (no Minecraft bootstrap) regression guard for two related bug classes found in
 * this codebase's {@link StructurePiece} subclasses:
 * <ul>
 *     <li><a href="https://github.com/quiqueck/BetterEnd/issues/594">BetterEnd #594</a>: {@code
 *     LakePiece}/{@code MountainPiece} cached per-column heights in a plain {@code HashMap} field.
 *     Concurrent unsynchronized writes from parallel worldgen (e.g. c2me) corrupted the HashMap's
 *     internal tree and left a worker thread spinning forever inside
 *     {@code HashMap$TreeNode.find}, wedging chunk generation.</li>
 *     <li><a href="https://github.com/quiqueck/BetterNether/issues/309">BetterNether #309</a>: {@code
 *     CityPiece}/{@code DestructionPiece} kept one reusable {@code MutableBlockPos} scratch cursor as
 *     an instance field. Two worker threads interleaving {@code POS.set(...)}/reads on that shared
 *     cursor produced no crash, just silently misplaced blocks.</li>
 * </ul>
 * Both share one root cause: a {@link StructurePiece} instance can be shared by a multi-chunk
 * structure and have {@code postProcess()} (or accessors it calls) invoked once per intersecting
 * chunk; under parallel worldgen those calls can run concurrently on different worker threads. Any
 * mutable, non-thread-safe object that outlives a single {@code postProcess()} call as an instance
 * field - a plain collection, a reused cursor, anything else with mutator methods - is subject to it.
 * <p>
 * Rather than pin the fix to those specific fields (which would miss the same mistake being
 * reintroduced elsewhere, in this class or a new one), this scans every compiled {@link
 * StructurePiece} subclass in this package for the general anti-pattern: an instance field assigned,
 * in a constructor, either (a) a non-thread-safe JDK collection, or (b) a {@code MutableBlockPos}. Both
 * are precisely the shape a "give this piece some per-call scratch state" regression takes.
 * <p>
 * This is a static heuristic, not a proof - it does not (and cannot, in general) catch every shared
 * mutable state bug, e.g. a raw counter/flag field or a custom mutable class. See
 * LakePieceConcurrentHeightCacheGameTest (src/testmod) for a complementary dynamic check that
 * actually drives concurrent getHeightClamp() calls against a real LakePiece instance.
 */
class StructurePieceThreadSafetyAuditTest {
    // Concrete JDK collection implementations that are not safe for concurrent access.
    private static final Set<String> UNSAFE_CTOR_OWNERS = Set.of(
            "java/util/HashMap",
            "java/util/LinkedHashMap",
            "java/util/TreeMap",
            "java/util/Hashtable",
            "java/util/ArrayList",
            "java/util/LinkedList",
            "java/util/HashSet",
            "java/util/LinkedHashSet",
            "java/util/Vector"
    );
    // Guava static factories that return the same unsafe implementations above.
    private static final Set<String> UNSAFE_FACTORY_OWNERS = Set.of(
            "com/google/common/collect/Maps",
            "com/google/common/collect/Lists",
            "com/google/common/collect/Sets"
    );
    // Only flag fields declared through one of these (interface or concrete-unsafe) types, so a
    // HashMap built only as a transient step toward something else entirely isn't miscounted.
    private static final Set<String> CACHE_FIELD_DESCRIPTORS = Set.of(
            "Ljava/util/Map;",
            "Ljava/util/List;",
            "Ljava/util/Set;",
            "Ljava/util/Collection;"
    );
    // Mutable single-purpose "cursor" types meant for reuse within one call (see BetterNether #309).
    // No field-descriptor gate is needed here, unlike the collection check above: the owner type
    // alone (a mutable coordinate cursor being stored, not just constructed and thrown away) is
    // already the anti-pattern, regardless of what the field is declared as.
    private static final Set<String> MUTABLE_SCRATCH_OWNERS = Set.of(
            "net/minecraft/core/BlockPos$MutableBlockPos"
    );

    @Test
    void noStructurePieceSharesUnsynchronizedMutableStateAcrossChunks() throws IOException, URISyntaxException,
            ClassNotFoundException {
        final File packageDir = packageDirectory();
        final List<String> offenders = new ArrayList<>();

        for (final File classFile : classFilesIn(packageDir)) {
            final String simpleName = classFile.getName().substring(0, classFile.getName().length() - ".class".length());
            // Skip inner/anonymous classes ($...): they don't declare top-level instance fields of
            // their enclosing piece and won't independently resolve as a StructurePiece subclass.
            if (simpleName.contains("$")) continue;

            final String className = StructurePieceThreadSafetyAuditTest.class.getPackageName() + "." + simpleName;
            final Class<?> loaded = Class.forName(className, false, getClass().getClassLoader());
            if (!StructurePiece.class.isAssignableFrom(loaded)) continue;

            offenders.addAll(findUnsafeFields(classFile, simpleName));
        }

        if (!offenders.isEmpty()) {
            fail(
                    "Found StructurePiece field(s) that share unsynchronized mutable state across chunks:\n  - "
                            + String.join("\n  - ", offenders) + "\n"
                            + "A StructurePiece instance can be shared by a multi-chunk structure and have "
                            + "postProcess() (or accessors it calls) invoked once per intersecting chunk. Under "
                            + "parallel worldgen those calls can run concurrently on different worker threads. A "
                            + "non-thread-safe collection field can have its internal structure corrupted, wedging "
                            + "a worker in an infinite loop (see BetterEnd #594); a shared MutableBlockPos-style "
                            + "cursor field can have its writes interleaved between threads, silently misplacing "
                            + "blocks with no crash at all (see BetterNether #309). Use a ConcurrentHashMap (or "
                            + "equivalent) for a cache, and a local variable inside postProcess() (not a field) for "
                            + "a scratch cursor."
            );
        }
    }

    private static List<String> findUnsafeFields(File classFile, String simpleName) throws IOException {
        final ClassNode node = new ClassNode();
        try (var in = Files.newInputStream(classFile.toPath())) {
            new ClassReader(in).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }

        final List<String> found = new ArrayList<>();
        for (final MethodNode method : node.methods) {
            if (!"<init>".equals(method.name)) continue;

            for (final AbstractInsnNode insn : method.instructions) {
                if (!(insn instanceof FieldInsnNode field) || field.getOpcode() != Opcodes.PUTFIELD) continue;

                final AbstractInsnNode producer = previousRealInsn(insn);
                final String unsafeSource = CACHE_FIELD_DESCRIPTORS.contains(field.desc)
                        ? describeUnsafeCollectionProducer(producer)
                        : null;
                final String scratchSource = unsafeSource == null ? describeMutableScratchProducer(producer) : null;

                if (unsafeSource != null) {
                    found.add(simpleName + "." + field.name + " (assigned " + unsafeSource + " in <init> - #594)");
                } else if (scratchSource != null) {
                    found.add(simpleName + "." + field.name + " (assigned " + scratchSource + " in <init> - #309)");
                }
            }
        }
        return found;
    }

    private static String describeUnsafeCollectionProducer(AbstractInsnNode insn) {
        if (!(insn instanceof MethodInsnNode call)) return null;

        if (call.getOpcode() == Opcodes.INVOKESPECIAL
                && "<init>".equals(call.name)
                && UNSAFE_CTOR_OWNERS.contains(call.owner)) {
            return "new " + call.owner.replace('/', '.') + "()";
        }
        if (call.getOpcode() == Opcodes.INVOKESTATIC
                && UNSAFE_FACTORY_OWNERS.contains(call.owner)
                && call.name.startsWith("new")) {
            return call.owner.replace('/', '.') + "." + call.name + "()";
        }
        return null;
    }

    private static String describeMutableScratchProducer(AbstractInsnNode insn) {
        if (!(insn instanceof MethodInsnNode call)) return null;

        if (call.getOpcode() == Opcodes.INVOKESPECIAL
                && "<init>".equals(call.name)
                && MUTABLE_SCRATCH_OWNERS.contains(call.owner)) {
            return "new " + call.owner.replace('/', '.').replace('$', '.') + "()";
        }
        return null;
    }

    // Skips label/line-number/frame nodes, which SKIP_DEBUG/SKIP_FRAMES mostly already remove but not
    // always (control-flow-only labels survive both).
    private static AbstractInsnNode previousRealInsn(AbstractInsnNode from) {
        AbstractInsnNode cur = from.getPrevious();
        while (cur instanceof LabelNode || cur instanceof LineNumberNode || cur instanceof FrameNode) {
            cur = cur.getPrevious();
        }
        return cur;
    }

    private static File packageDirectory() throws URISyntaxException {
        // Deliberately anchored on a main-sourceSet class (not this test class itself): this class
        // compiles to build/classes/java/test, which is a different directory from the main output
        // that actually contains LakePiece.class et al.
        final File classesRoot = new File(
                LakePiece.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        );
        return new File(classesRoot, StructurePieceThreadSafetyAuditTest.class.getPackageName().replace('.', '/'));
    }

    private static List<File> classFilesIn(File dir) {
        final File[] files = dir.listFiles((f, name) -> name.endsWith(".class"));
        return files == null ? List.of() : Arrays.asList(files);
    }
}
