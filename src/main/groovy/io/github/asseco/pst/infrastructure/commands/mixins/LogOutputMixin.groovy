package io.github.asseco.pst.infrastructure.commands.mixins

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger
import picocli.CommandLine.Option

final class LogOutputMixin {
    private static final Logger logger = LoggerFactory.getLogger(LogOutputMixin.class)
    private String logFilePath

    @Option(names = ["-lt", "--log-to"], description = ["Specify a file to where you want the logging output to occur.", "Ex: \"C:\\log.txt\""])
    void setLogTo(String aLogFilePath = null) {
        this.logFilePath = aLogFilePath
        try {
            if (aLogFilePath) {
                logger.info("Logging to file ${this.logFilePath}...")
                LoggerFactory.setLoggerToFile(aLogFilePath)
            } else {
                logger.info("Logging to console...")
                LoggerFactory.setLoggerToConsole()
            }

        } catch (Exception exception) {
            logger.error("Unable to set log output. Cause: ${exception}")
            System.exit(1)
        }
    }

    String getLogFilePath() {
        return this.logFilePath
    }
}
