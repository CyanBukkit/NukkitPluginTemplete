/*
 * Copyright (c) 2026.
 * # 太霄玉府五雷使院镇煞符
 * # 敕令：诸 BUG 急退，急急如律令！
 * #
 * # 雷  火  雷
 * #    部  令
 * # 雷  火  雷
 * #
 * # 净天地神咒（节选）
 * # 天地自然，秽气分散，洞中玄虚，晃朗太元；
 * # 八方威神，使我自然，灵宝符命，普告九天。
 * #
 * # 本代码受太上老君、九天应元雷声普化天尊庇佑，
 * # 如生 BUG，则坎离交泰，雷火丹成，BUG 自化虚无。
 *
 */

package cn.nukkitmot.exampleplugin.loader;

import cn.nukkit.plugin.Plugin;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class NukkitLibraryManager extends LibraryManager {
    private final Plugin plugin;
    private final List<URLClassLoader> classLoaders = new ArrayList<>();
    private volatile URLClassLoader pluginClassLoader;

    public NukkitLibraryManager(Plugin plugin) {
        this(plugin, "libs");
    }

    public NukkitLibraryManager(Plugin plugin, String directoryName) {
        super(new File("").toPath(), directoryName);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        addRepository(Repositories.MAVEN_CENTRAL);
        addRepository(Repositories.JITPACK);
    }

    @Override
    protected void addToClasspath(Path file) {
        try {
            URL url = file.toUri().toURL();
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{url},
                    plugin.getClass().getClassLoader()
            );
            classLoaders.add(classLoader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add library to classpath: " + file, e);
        }
    }

    public void loadLibraryAndDependencies(Library library) {
        loadLibrary(library);
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        for (URLClassLoader classLoader : classLoaders) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException("Class not found: " + className);
    }

    public void close() {
        for (URLClassLoader classLoader : classLoaders) {
            try {
                classLoader.close();
            } catch (Exception ignored) {
            }
        }
        classLoaders.clear();
        if (pluginClassLoader != null) {
            try {
                pluginClassLoader.close();
            } catch (Exception ignored) {
            }
            pluginClassLoader = null;
        }
    }
}
