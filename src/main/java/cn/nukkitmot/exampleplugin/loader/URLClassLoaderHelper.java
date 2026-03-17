package cn.nukkitmot.exampleplugin.loader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;

public class URLClassLoaderHelper {

    private static final sun.misc.Unsafe theUnsafe;

    static {
        sun.misc.Unsafe unsafe = null;
        for (Field f : sun.misc.Unsafe.class.getDeclaredFields()) {
            try {
                if (f.getType() == sun.misc.Unsafe.class && Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    unsafe = (sun.misc.Unsafe) f.get(null);
                }
            } catch (Exception ignored) {
            }
        }
        theUnsafe = unsafe;
    }

    private final URLClassLoader classLoader;
    private MethodHandle addURLMethodHandle = null;

    public URLClassLoaderHelper(URLClassLoader classLoader, LibraryManager libraryManager) {
        Objects.requireNonNull(libraryManager, "libraryManager");
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");

        try {
            Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

            try {
                openUrlClassLoaderModule();
            } catch (Exception ignored) {
            }

            try {
                addURLMethod.setAccessible(true);
            } catch (Exception exception) {
                if (exception.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
                    if (theUnsafe != null) {
                        try {
                            addURLMethodHandle = getPrivilegedMethodHandle(addURLMethod).bindTo(classLoader);
                            return;
                        } catch (Exception ignored) {
                            addURLMethodHandle = null;
                        }
                    }
                    try {
                        addOpensWithAgent(libraryManager);
                        addURLMethod.setAccessible(true);
                    } catch (Exception e) {
                        System.err.println("Cannot access URLClassLoader#addURL(URL), if you are using Java 9+ try to add the following option to your java command: --add-opens java.base/java.net=ALL-UNNAMED");
                        throw new RuntimeException("Cannot access URLClassLoader#addURL(URL)", e);
                    }
                } else {
                    throw new RuntimeException("Cannot set accessible URLClassLoader#addURL(URL)", exception);
                }
            }
            this.addURLMethodHandle = MethodHandles.lookup().unreflect(addURLMethod).bindTo(classLoader);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToClasspath(URL url) {
        try {
            addURLMethodHandle.invokeWithArguments(Objects.requireNonNull(url, "url"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void addToClasspath(Path path) {
        try {
            addToClasspath(Objects.requireNonNull(path, "path").toUri().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void openUrlClassLoaderModule() throws Exception {
        Class<?> moduleClass = Class.forName("java.lang.Module");
        Method getModuleMethod = Class.class.getMethod("getModule");
        Method addOpensMethod = moduleClass.getMethod("addOpens", String.class, moduleClass);

        Object urlClassLoaderModule = getModuleMethod.invoke(URLClassLoader.class);
        Object thisModule = getModuleMethod.invoke(URLClassLoaderHelper.class);

        addOpensMethod.invoke(urlClassLoaderModule, URLClassLoader.class.getPackage().getName(), thisModule);
    }

    private static MethodHandle getPrivilegedMethodHandle(Method method) throws Exception {
        for (Field trustedLookup : MethodHandles.Lookup.class.getDeclaredFields()) {
            if (trustedLookup.getType() != MethodHandles.Lookup.class || !Modifier.isStatic(trustedLookup.getModifiers()) || trustedLookup.isSynthetic()) {
                continue;
            }

            try {
                MethodHandles.Lookup lookup = (MethodHandles.Lookup) theUnsafe.getObject(theUnsafe.staticFieldBase(trustedLookup), theUnsafe.staticFieldOffset(trustedLookup));
                return lookup.unreflect(method);
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException("Cannot get privileged method handle.");
    }

    private void addOpensWithAgent(LibraryManager libraryManager) throws Exception {
        IsolatedClassLoader isolatedClassLoader = new IsolatedClassLoader();
        try {
            isolatedClassLoader.addPath(libraryManager.downloadLibrary(
                    Library.builder()
                            .groupId("net.bytebuddy")
                            .artifactId("byte-buddy-agent")
                            .version("1.12.1")
                            .checksum("mcCtBT9cljUEniB5ESpPDYZMfVxEs1JRPllOiWTP+bM=")
                            .repository(Repositories.MAVEN_CENTRAL)
                            .build()
            ));

            Class<?> byteBuddyAgent = isolatedClassLoader.loadClass("net.bytebuddy.agent.ByteBuddyAgent");

            Object instrumentation = byteBuddyAgent.getDeclaredMethod("install").invoke(null);
            Class<?> instrumentationClass = Class.forName("java.lang.instrument.Instrumentation");
            Method redefineModule = instrumentationClass.getDeclaredMethod("redefineModule", Class.forName("java.lang.Module"), java.util.Set.class, java.util.Map.class, java.util.Map.class, java.util.Set.class, java.util.Map.class);
            Method getModule = Class.class.getDeclaredMethod("getModule");
            java.util.Map<String, java.util.Set<?>> toOpen = java.util.Collections.singletonMap("java.net", java.util.Collections.singleton(getModule.invoke(getClass())));
            redefineModule.invoke(instrumentation,
                    getModule.invoke(URLClassLoader.class),
                    java.util.Collections.emptySet(),
                    java.util.Collections.emptyMap(),
                    toOpen,
                    java.util.Collections.emptySet(),
                    java.util.Collections.emptyMap());
        } finally {
            try {
                isolatedClassLoader.close();
            } catch (Exception ignored) {
            }
        }
    }
}
