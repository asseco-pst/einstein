package io.github.asseco.pst.infrastructure.logs

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.FileAppender
import org.apache.log4j.PatternLayout
import org.apache.log4j.Level
import org.slf4j.Logger

final class LoggerFactory {
    private final static String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1} - %m%n"

    static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz)
    }

    static void setLoggerToConsole() {
        getRootLogger().removeAllAppenders()
        getRootLogger().addAppender(generateConsoleAppender())
    }

    static void setLoggerToFile(String aFilePath) {
        getRootLogger().removeAllAppenders()
        getRootLogger().addAppender(generateFileAppender(aFilePath))
    }

    static void setLogLevel(Level level) {
        getRootLogger().setLevel(level)
    }

    private static org.apache.log4j.Logger getRootLogger() {
        return org.apache.log4j.Logger.getRootLogger();
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
