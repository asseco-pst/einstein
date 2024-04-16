package io.github.asseco.pst.commands


import io.github.asseco.pst.commands.mixins.SaveToFileMixin
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.ProjectDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine.Mixin

abstract class AbstractEinsteinCommand {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractEinsteinCommand.class)
    protected static final String GITLAB_URL = "GITLAB_URL"
    protected static final String GITLAB_TOKEN = "GITLAB_TOKEN"

    @Mixin
    protected SaveToFileMixin saveToFileMixin = new SaveToFileMixin(logger)

    /**
     * Calculates a list of dependencies for a given project
     *
     * @param projects
     */
    protected void calculateDependencies(List<ProjectDao> projects) {
        Einstein einstein = new Einstein()
        try {
            logger.info("Checking if the necessary environment variables where setup...")
            checkGitlabEnvVariables()

            logger.info("Calculating dependencies for provided projects...")
            Map<String, String> parsedDependencies = einstein.calcDependencies(projects)

            logger.info("Finishing up...")

            einstein.shutdown()
            handleParsedDependencies(parsedDependencies)

        } catch (Exception exception) {
            logger.error("Could not finish the dependencies calculation. Cause: ${exception}")
            logger.debug("Exception thrown: ", exception)

            einstein.shutdown()
            System.exit(401)
        }

        logger.info("Dependencies calculations finished successfully!")
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

    private static void checkGitlabEnvVariables() {
        if (!System.getenv(GITLAB_URL) || !System.getenv(GITLAB_TOKEN)) {
            throw new IllegalArgumentException("Environment variables '$GITLAB_URL' and/or '$GITLAB_TOKEN' are undefined. Please set them before trying to run again...")
        }
    }
}
