package io.github.asseco.pst.commands.logs

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.core.ConsoleAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

final class CustomLoggerFactory {
    private final static String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1} - %m%n"
    private final static Level DEFAULT_LEVEL = Level.INFO
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    static Logger getLogger(Class<?> aClazz) {
        return LoggerFactory.getLogger(aClazz)
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

    private static ch.qos.logback.classic.Logger getRootLogger() {
        return (ch.qos.logback.classic.Logger)loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)
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
