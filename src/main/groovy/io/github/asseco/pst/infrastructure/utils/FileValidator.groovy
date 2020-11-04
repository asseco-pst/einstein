package io.github.asseco.pst.infrastructure.utils

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger

import java.nio.file.InvalidPathException
import java.nio.file.Paths

final class FileValidator {
    private String filePath
    private String fileExtension
    private Logger logger = LoggerFactory.getLogger(FileValidator.class)


    FileValidator setFilePath(String filePath) {
        this.filePath = filePath
        return this
    }

    FileValidator setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension
        return this
    }

    FileValidator setLogger(Logger logger) {
        this.logger = logger
        return this
    }

    /**
     * Validates if the provided file path is valid. Also verifies if it includes an extension, if given
     * @return boolean
     */
    boolean validate() {
        try {
            Paths.get(this.filePath)
        } catch (InvalidPathException | NullPointerException ignored) {
            logger.warn("The provided file \"${this.filePath}\" path isn't valid!")
            return false
        }

        if (this.fileExtension) {
            return this.fileExtension == this.getFilePathExtension()
        }

        return true
    }

    /**
     * Gets the file extension
     *
     * @param filename
     * @return the file extension
     */
    String getFilePathExtension() {
        if (this.filePath.lastIndexOf(".") > 0) {
            return filePath.substring(filePath.lastIndexOf(".") + 1)
        }
        return ''
    }
}