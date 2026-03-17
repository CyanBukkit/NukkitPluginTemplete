package cn.nukkitmot.exampleplugin.loader.Logger;

public interface LogAdapter {
    void log(LogLevel level, String message);
    default void info(String message) { log(LogLevel.INFO, message); }
    default void warn(String message) { log(LogLevel.WARN, message); }
    default void error(String message) { log(LogLevel.ERROR, message); }
    default void debug(String message) { log(LogLevel.DEBUG, message); }
}
