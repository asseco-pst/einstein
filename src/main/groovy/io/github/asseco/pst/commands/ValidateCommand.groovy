package io.github.asseco.pst.commands

import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.ProjectDao
import io.github.asseco.pst.infrastructure.utils.FileValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option

@Command(name = "validate", description = "This command validates the necessary einstein dependencies for a project", subcommands = [HelpCommand.class])
final class ValidateCommand extends AbstractEinsteinCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ValidateCommand.class)
    private String inputFile

    @Option(names = ["-i", "--input"], paramLabel = "<project_folder>", required = true, description = "Project folder that contains the einstein.yaml file for which the dependencies must be validated")
    void setInputFile(String aInputFile) {

        FileValidator fileValidator = new FileValidator()
                .setFilePath("${aInputFile.trim()}/${Project.EINSTEIN_FILENAME}")
                .setFileExtension("yaml")
                .setLogger(logger)

        if (!fileValidator.validate()) {
            logger.error("The provided file path \"${aInputFile}\" or extension is not valid!")
            System.exit(301)
        }

        this.inputFile = "${aInputFile.trim()}/${Project.EINSTEIN_FILENAME}".trim()
    }

    @Override
    void run() {
        this.calculateDependencies(generateProjectsDao(this.inputFile))
    }

    /**
     * Generates a list of project DAO given a valid YAML file
     *
     * @param aInputFile the path to the YAML file
     * @return List<ProjectDao>       A list of project DAO
     */
    private static List<ProjectDao> generateProjectsDao(String aInputFile) {
        try {
            Reader einsteinFileContent = new InputStreamReader(new FileInputStream(aInputFile))
            Map<String, Object> parsedContent = (new Yaml()).load(einsteinFileContent)
            List<ProjectDao> projects = []

            parsedContent.each { String namespace, Object project ->
                List<Map> projectProperties = project as List<Map>

                projectProperties.each { Map projectProperty ->
                    projects.add(
                            generateProjectDao(namespace, projectProperty)
                    )
                }
            }

            return projects
        } catch (Exception exception) {
            logger.error("Could not generate the necessary information from the provided file. Cause: ${exception.getMessage()}")
            System.exit(1)
        }
        return []
    }

    /**
     * Generates individual project DAO according to the Map property
     *
     * @param aNamespace The project namespace
     * @param aProjectProperty The Map property
     * @return ProjectDao The resulting project DAO
     */
    private static ProjectDao generateProjectDao(String aNamespace, Map aProjectProperty) {
        return new ProjectDao(
                aProjectProperty.keySet().first().toString().trim(),
                aNamespace.trim(),
                aProjectProperty.values().first().toString().trim()
        )
    }
}
