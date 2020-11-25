package io.github.asseco.pst.commands.mixins

import groovy.json.JsonBuilder
import io.github.asseco.pst.infrastructure.utils.FileValidator
import org.slf4j.Logger
import picocli.CommandLine.Option

final class SaveToFileMixin {
    private Logger logger

    private String saveToFilePath

    SaveToFileMixin(Logger aLogger) {
        this.logger = aLogger
    }

    @Option(names = ["-o", "--output"], paramLabel = "<filepath>", description = [" (OPTIONAL) The file to where the results should be outputted.", "Ex: \"C:\\dependencies.txt\""])
    void setSaveToFilePath(String aSaveToFilePath) {
        FileValidator fileValidator = new FileValidator()
                .setFilePath(aSaveToFilePath)
                .setLogger(logger)

        if (!fileValidator.validate()) {
            this.logger.error("The provided file path \"${aSaveToFilePath}\" is not valid!")
            System.exit(201)
        }

        this.saveToFilePath = aSaveToFilePath.toString().trim()
    }

    String getSaveToFilePath() {
        return saveToFilePath
    }

    void writeToSaveFile(Map<String, String> aParsedDeps) {
        try {
            this.logger.info("Writing calculated dependencies into file ${this.saveToFilePath}...")
            new File(this.saveToFilePath).write(new JsonBuilder(aParsedDeps).toPrettyString())
        } catch (Exception exception) {
            this.logger.error("Unable to save results into output file '${this.saveToFilePath}'. Cause: ${exception.getMessage()}")
            System.exit(202)
        }
    }
}
