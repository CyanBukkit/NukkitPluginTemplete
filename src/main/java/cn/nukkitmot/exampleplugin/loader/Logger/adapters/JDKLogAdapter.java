package cn.nukkitmot.exampleplugin.loader.Logger.adapters;

import cn.nukkitmot.exampleplugin.loader.Logger.LogAdapter;
import cn.nukkitmot.exampleplugin.loader.Logger.LogLevel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDKLogAdapter implements LogAdapter {
    private final Logger logger;

    public JDKLogAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(LogLevel level, String message) {
        Level jdkLevel = switch (level) {
            case DEBUG -> Level.FINE;
            case INFO -> Level.INFO;
            case WARN -> Level.WARNING;
            case ERROR -> Level.SEVERE;
        };
        logger.log(jdkLevel, message);
    }
}
