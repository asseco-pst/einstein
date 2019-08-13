package io.github.asseco.pst


import groovy.json.JsonBuilder
import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.cli.CliParser
import io.github.asseco.pst.infrastructure.utils.Console

class Main {

    static final String GITLAB_URL = "GITLAB_URL"
    static final String GITLAB_TOKEN = "GITLAB_TOKEN"

    static CliParser cliParser

    static void main(String[] args) {

        cliParser = Einstein.getCli()

        try {
            cliParser.parse(args)
        } catch (e) {
            Console.err(e.getMessage())
            cliParser.cli.usage()
            System.exit(1)
        }

        try {
            // set the repository to look for
            checkGitlabEnvVariables()
            RepoExplorerFactory.create(RepoExplorerFactory.Type.GITLAB, System.getenv(GITLAB_URL), System.getenv(GITLAB_TOKEN))
            Einstein.calcDependencies(cliParser.einsteinOptions.projects)

            String outputFilePath = cliParser.einsteinOptions.saveToFile
            if (outputFilePath)
                saveResultsIntoFile(outputFilePath)

        } catch (e) {
            interrupt(e)
        }

        Console.print("Finished!")
    }

    static private void checkGitlabEnvVariables() {

        if(!System.getenv(GITLAB_URL))
            throw new IllegalArgumentException("Environment variable '$GITLAB_URL' is undefined.")
        if(!System.getenv(GITLAB_TOKEN))
            throw new IllegalArgumentException("Environment variable '$GITLAB_TOKEN' is undefined.")

    }

    static private void saveResultsIntoFile(String aFilePath) {

        try {
            Console.info("Saving dependencies into file ${aFilePath}")
            new File(aFilePath).write(new JsonBuilder(Einstein.getCollectedDependencies()).toPrettyString())
        } catch (e) {
            Console.err("Unable to save results into output file '${aFilePath}'. Cause: ${e}")
            System.exit(1)
        }
    }

    static void interrupt(Exception aException) {

        Console.err("An error occurred. Cause: ${aException}")
        Console.err(aException.printStackTrace())

        System.exit(1)
    }
}
