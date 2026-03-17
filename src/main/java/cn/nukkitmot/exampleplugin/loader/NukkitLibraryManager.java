package cn.nukkitmot.exampleplugin.loader;

import cn.nukkit.plugin.Plugin;
import cn.nukkitmot.exampleplugin.loader.Logger.adapters.JDKLogAdapter;
import lombok.Getter;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

@Getter
public class NukkitLibraryManager extends LibraryManager {
    private final Plugin plugin;
    private final URLClassLoaderHelper classLoaderHelper;

    public NukkitLibraryManager(Plugin plugin) {
        this(plugin, "libs");
    }

    public NukkitLibraryManager(Plugin plugin, String directoryName) {
        super(new JDKLogAdapter(Logger.getLogger(Objects.requireNonNull(plugin, "plugin").getName())),
                plugin.getDataFolder().toPath(), directoryName);
        URLClassLoader pluginClassLoader = (URLClassLoader) plugin.getClass().getClassLoader();
        this.classLoaderHelper = new URLClassLoaderHelper(pluginClassLoader, this);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        addMavenCentral();
        addJitPack();
    }

    @Override
    protected void addToClasspath(Path file) {
        this.classLoaderHelper.addToClasspath(file);
    }
}
