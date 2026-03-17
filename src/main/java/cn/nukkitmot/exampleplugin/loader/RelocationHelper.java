package cn.nukkitmot.exampleplugin.loader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class RelocationHelper {
    private final Constructor<?> jarRelocatorConstructor;
    private final Method jarRelocatorRunMethod;
    private final Constructor<?> relocationConstructor;

    public RelocationHelper(LibraryManager libraryManager) {
        Objects.requireNonNull(libraryManager, "libraryManager");
        IsolatedClassLoader classLoader = new IsolatedClassLoader();
        classLoader.addPath(libraryManager.downloadLibrary(Library.builder()
                .groupId("org.ow2.asm")
                .artifactId("asm-commons")
                .version("9.2")
                .checksum("vkzlMTiiOLtSLNeBz5Hzulzi9sqT7GLUahYqEnIl4KY=")
                .repository(Repositories.MAVEN_CENTRAL)
                .build()));
        classLoader.addPath(libraryManager.downloadLibrary(Library.builder()
                .groupId("org.ow2.asm")
                .artifactId("asm")
                .version("9.2")
                .checksum("udT+TXGTjfOIOfDspCqqpkz4sxPWeNoDbwyzyhmbR/U=")
                .repository(Repositories.MAVEN_CENTRAL)
                .build()));
        classLoader.addPath(libraryManager.downloadLibrary(Library.builder()
                .groupId("me.lucko")
                .artifactId("jar-relocator")
                .version("1.5")
                .checksum("0D6eM99gKpEYFNDydgnto3Df0ygZGdRVqy5ahtj0oIs=")
                .repository(Repositories.MAVEN_CENTRAL)
                .build()));
        try {
            Class<?> jarRelocatorClass = classLoader.loadClass("me.lucko.jarrelocator.JarRelocator");
            Class<?> relocationClass = classLoader.loadClass("me.lucko.jarrelocator.Relocation");
            this.jarRelocatorConstructor = jarRelocatorClass.getConstructor(File.class, File.class, Collection.class);
            this.jarRelocatorRunMethod = jarRelocatorClass.getMethod("run");
            this.relocationConstructor = relocationClass.getConstructor(String.class, String.class, Collection.class, Collection.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void relocate(Path in, Path out, Collection<Relocation> relocations) {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        Objects.requireNonNull(relocations, "relocations");
        try {
            LinkedList<Object> rules = new LinkedList<>();
            for (Relocation relocation : relocations) {
                rules.add(this.relocationConstructor.newInstance(
                        relocation.getPattern(),
                        relocation.getRelocatedPattern(),
                        relocation.getIncludes(),
                        relocation.getExcludes()));
            }
            this.jarRelocatorRunMethod.invoke(this.jarRelocatorConstructor.newInstance(in.toFile(), out.toFile(), rules));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
