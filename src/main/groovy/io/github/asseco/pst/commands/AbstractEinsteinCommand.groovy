package io.github.asseco.pst.commands

import io.github.asseco.pst.commands.mixins.LogOutputMixin
import io.github.asseco.pst.commands.mixins.SaveToFileMixin
import io.github.asseco.pst.commands.mixins.VerboseMixin
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.ProjectDao
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger
import picocli.CommandLine.Mixin

abstract class AbstractEinsteinCommand {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractEinsteinCommand.class)

    @Mixin
    protected SaveToFileMixin saveToFileMixin = new SaveToFileMixin(logger)
    @Mixin
    protected LogOutputMixin logOutputMixin = new LogOutputMixin(logger)
    @Mixin
    protected VerboseMixin verboseMixin

    /**
     * Calculates a list of dependencies for a given project
     *
     * @param projects
     */
    protected void calculateDependencies(List<ProjectDao> projects) {
        try {
            logger.info("Calculating dependencies for provided projects...")

            Map<String, String> parsedDependencies = Einstein.instance.calcDependencies(projects)
            handleParsedDependencies(parsedDependencies)
        } catch (Exception exception) {
            logger.error("Could not finish the dependencies calculation. Cause: ${exception}")
            System.exit(401)
        }
        logger.info("Dependencies calculations finished successfully")
    }

    /**
     * Checks if the dependencies should be written to an output file and, if so, writes them.
     *
     * @param aParsedDependencies
     */
    protected void handleParsedDependencies(Map<String, String> aParsedDependencies) {
        if (saveToFileMixin.saveToFilePath) {
            saveToFileMixin.writeToSaveFile(aParsedDependencies)
        }
    }
}
