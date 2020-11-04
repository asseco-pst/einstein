package io.github.asseco.pst.commands.mixins

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import io.github.asseco.pst.infrastructure.utils.FileValidator
import org.slf4j.Logger
import picocli.CommandLine.Option

final class LogOutputMixin {
    private Logger logger

    LogOutputMixin(Logger aLogger) {
        this.logger = aLogger
    }

    @Option(names = ["-lt", "--log-to"], paramLabel = "<filepath>", description = ["(OPTIONAL) Specify a file to where you want the logging output to occur.", "Ex: \"C:\\log.txt\""])
    void setLogFilePath(String aLogFilePath) {
        FileValidator fileValidator = new FileValidator()
                .setFilePath(aLogFilePath)
                .setLogger(logger)

        if (fileValidator.validate()) {
            this.logger.error("The provided file path \"${aLogFilePath}\" is not valid!")
            System.exit(101)
        }

        try {
            this.logger.info("Logging to file ${aLogFilePath.toString().trim()}...")
            LoggerFactory.setLoggerToFile(aLogFilePath.toString().trim())

        } catch (Exception exception) {
            this.logger.error("Unable to set log output. Cause: ${exception}")
            System.exit(102)
        }
    }
}
