package hotel.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {

    private static LoggerUtil instance;
    private static Logger logger;

    private LoggerUtil() {
        logger = Logger.getLogger("HotelAppLogger");
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new SimpleFormatter());

        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    public static LoggerUtil getInstance() {
        if (instance == null) {
            instance = new LoggerUtil();
        }
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }
}