package com.azzgil.coeditor.utils.logging;

import java.util.logging.Logger;

public class ColoredLogger {

    private Logger logger;

    public ColoredLogger(String loggerFor) {
        logger = Logger.getLogger(loggerFor);
    }

    public void error(String message) {
        logger.warning(Colors.RED + message + Colors.RESET + "\n");
    }

    public void info(String message) {
        logger.info(Colors.GREEN + message + Colors.RESET + "\n");
    }

    public void warning(String message) {
        logger.warning(Colors.YELLOW + message + Colors.RESET + "\n");
    }
}
