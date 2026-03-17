package cn.nukkitmot.exampleplugin.loader.Logger;

import cn.nukkitmot.exampleplugin.loader.Logger.adapters.JDKLogAdapter;

import java.util.Objects;

public class NukkitLogger {
    private final LogAdapter adapter;
    private LogLevel level = LogLevel.INFO;

    public NukkitLogger(LogAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    public static NukkitLogger getLogger(String name) {
        return new NukkitLogger(new JDKLogAdapter(java.util.logging.Logger.getLogger(name)));
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() >= this.level.ordinal()) {
            adapter.log(level, message);
        }
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = Objects.requireNonNull(level, "level");
    }
}
