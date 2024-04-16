package io.github.asseco.pst.commands

import io.github.asseco.pst.infrastructure.ProjectDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option

@Command(name = "calculate", description = "This command calculates the necessary einstein dependencies", subcommands = [HelpCommand.class])
final class CalculateCommand extends AbstractEinsteinCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CalculateCommand.class)
    private List<ProjectDao> projects

    @Option(names = ["-p", "--projects"], paramLabel = "<projects>", required = true, description = "Receives a list of projects for which the dependencies must be calculated")
    void setProjects(String aProjects) {
        try {
            this.projects = aProjects.tokenize(",").stream().collect({ String project ->
                ProjectDao.fromFullName(project)
            })

            if (this.projects?.size() == 0) {
                logger.error("No projects where found for the provided '${aProjects}'.")
                System.exit(1)
            }

        } catch (Exception exception) {
            logger.error("Unable to obtain the project list for the provided projects '${aProjects}'. Cause: ${exception}")
            System.exit(1)
        }
    }

    @Override
    void run() {
        this.calculateDependencies(this.projects)
    }
}
