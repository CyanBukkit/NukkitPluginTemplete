package cn.nukkitmot.exampleplugin.loader.Logger;

import cn.nukkitmot.exampleplugin.loader.Logger.adapters.JDKLogAdapter;

import java.util.Objects;

public class LibLogger {
    private final LogAdapter adapter;
    private LogLevel level = LogLevel.INFO;

    public LibLogger(LogAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    public static LibLogger getLogger(String name) {
        java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger(name);
        jdkLogger.setParent(java.util.logging.Logger.getLogger(""));
        return new LibLogger(new JDKLogAdapter(jdkLogger));
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
