package io.github.asseco.pst.infrastructure.commands

import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.ProjectDao
import io.github.asseco.pst.infrastructure.commands.mixins.LogOutputMixin
import io.github.asseco.pst.infrastructure.commands.mixins.SaveToFileMixin
import io.github.asseco.pst.infrastructure.commands.mixins.VerboseMixin
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option

@Command(name = "calculate", description = "This command allows calculate the necessary einstein dependencies", subcommands = [HelpCommand.class])
class CalculateCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CalculateCommand.class)
    private List<ProjectDao> projects

    @Mixin
    private SaveToFileMixin saveToFileMixin
    @Mixin
    private LogOutputMixin logOutputMixin
    @Mixin
    private VerboseMixin verboseMixin

    @Option(names = ["-p", "--projects"], required = true, description = "Receives a list of projects for which the dependencies must be calculated")
    void setProjects(String aProjects) {
        try {
            this.projects = aProjects.tokenize(",").stream().collect({ String project ->
                ProjectDao.fromFullName(project)
            })

            if(this.projects?.size() == 0) {
                logger.error("No projects where found for the provided '${aProjects}'.")
                System.exit(1)
            }

        } catch (Exception exception) {
            logger.error("Unable to obtain the project list for the provided projects '${aProjects}'. Cause: ${exception}")
            System.exit(1)
        }
    }

    List<ProjectDao> getProjects() {
        return this.projects
    }

    @Override
    void run() {
        try {
            logger.info("Calculating dependencies for provided projects...")

            Map<String, String> parsedDependencies = Einstein.instance.calcDependencies(this.projects)
            handleParsedDependencies(parsedDependencies)
        } catch (Exception exception) {
            logger.error("Could not finished the dependencies calculation. Cause: ${exception}", exception)
            System.exit(1)
        }
        logger.info("Dependencies calculations finished successfully")
    }

    private void handleParsedDependencies(Map<String, String> aParsedDependencies) {
        if (saveToFileMixin.saveToFilePath) {
            saveToFileMixin.writeToSaveFile(aParsedDependencies)
        }
    }
}
