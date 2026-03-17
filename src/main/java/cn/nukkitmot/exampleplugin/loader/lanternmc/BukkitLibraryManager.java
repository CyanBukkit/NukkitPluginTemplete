package cn.nukkitmot.exampleplugin.loader.lanternmc;

import cn.nukkit.plugin.Plugin;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class BukkitLibraryManager extends LibraryManager {
    private final Plugin plugin;

    public BukkitLibraryManager(Plugin plugin) {
        this(plugin, "libs");
    }

    public BukkitLibraryManager(Plugin plugin, String directoryName) {
        super(new File("").toPath(), directoryName);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    protected void addToClasspath(Path file) {
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
