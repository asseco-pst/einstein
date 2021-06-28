package io.github.asseco.pst.commands.logs

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.FileAppender
import org.apache.log4j.Level
import org.apache.log4j.PatternLayout
import org.slf4j.Logger

final class LoggerFactory {
    private final static String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1} - %m%n"
    private final static Level DEFAULT_LEVEL = Level.INFO

    static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz)
    }

    /**
     * Sets logging to console
     */
    static void setLoggerToConsole() {
        getRootLogger().with {
            removeAllAppenders()
            addAppender(generateConsoleAppender())
            setLevel(DEFAULT_LEVEL)
        }
    }

    /**
     * Sets logging to the provided file
     * @param aFilePath
     */
    static void setLoggerToFile(String aFilePath) {
        getRootLogger().with {
            removeAllAppenders()
            addAppender(generateFileAppender(aFilePath))
            setLevel(DEFAULT_LEVEL)
        }
    }

    /**
     * Sets log level
     * @param level
     */
    static void setLogLevel(Level level) {
        getRootLogger().setLevel(level)
    }

    private static org.apache.log4j.Logger getRootLogger() {
        return org.apache.log4j.Logger.getRootLogger()
    }

    private static ConsoleAppender generateConsoleAppender() {
        ConsoleAppender console = new ConsoleAppender()

        console.with {
            setLayout(new PatternLayout(PATTERN))
            activateOptions()
        }

        return console
    }

    private static FileAppender generateFileAppender(String aFilePath) {
        FileAppender file = new FileAppender()

        file.with {
            setLayout(new PatternLayout(PATTERN))
            setFile(aFilePath.trim())
            setAppend(true)
            activateOptions()
        }

        return file
    }
}
