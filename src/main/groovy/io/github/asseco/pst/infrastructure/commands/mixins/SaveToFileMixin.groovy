package io.github.asseco.pst.infrastructure.commands.mixins

import groovy.json.JsonBuilder
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger
import picocli.CommandLine.Option

import java.nio.file.InvalidPathException
import java.nio.file.Paths

final class SaveToFileMixin {
    private static final Logger logger = LoggerFactory.getLogger(SaveToFileMixin.class)
    private String saveToFilePath

    @Option(names = ["-o", "--output"], description = ["The file to where the results should be outputted.", "Ex: \"C:\\dependencies.txt\""])
    void setSaveToFilePath(String aSaveToFilePath) {
        this.saveToFilePath = aSaveToFilePath.toString().trim()

        if(!isValidPath()) {
            logger.error("The provided output file path is not valid!")
            System.exit(1)
        }
    }

    String getSaveToFilePath() {
        return this.saveToFilePath
    }

    void writeToSaveFile(Map<String, String> aParsedDeps) {
        try {
            logger.info("Writing calculated dependencies into file ${this.saveToFilePath}")
            new File(this.saveToFilePath).write(new JsonBuilder(aParsedDeps).toPrettyString())
        } catch (Exception exception) {
            logger.error("Unable to save results into output file '${this.saveToFilePath}'. Cause: ${exception}")
            System.exit(1)
        }
    }

    private boolean isValidPath() {
        try {
            Paths.get(this.saveToFilePath)
        } catch (InvalidPathException | NullPointerException ignored) {
            return false
        }
        return true
    }
}
