package com.assecopst.channels.devops

import com.assecopst.channels.devops.infrastructure.RDMApp
import com.assecopst.channels.devops.infrastructure.cli.CliParser
import com.assecopst.channels.devops.infrastructure.utils.Console
import groovy.json.JsonBuilder

class Main {

    static CliParser cliParser

    static void main(String[] args) {

        cliParser = new CliParser()

        try {
            cliParser.parse(args)
        } catch (e) {
            Console.err(e.getMessage())
            cliParser.cli.usage()
            System.exit(1)
        }

        try {
            RDMApp.calcDependencies(cliParser.rdmOptions.projects)

            if (cliParser.rdmOptions.saveToFile)
                RDMApp.saveResultsToJsonFile()

            String outputFilePath = cliParser.rdmOptions.saveToFile
            if (outputFilePath)
                saveResultsIntoFile(outputFilePath)

        } catch (e) {
            Console.err("Unable to calculate provided Projects Runtime dependencies. Cause: ${e}")
            System.exit(1)
        }

        Console.print("Finished!")
    }

    static private void saveResultsIntoFile(String aFilePath) {

        try {
            new File(aFilePath).write(new JsonBuilder(RDMApp.dpManager.finalDependencies).toPrettyString())
        } catch (e) {
            Console.err("Unable to save results into output file '${aFilePath}'. Cause: ${e}")
            System.exit(1)
        }
    }
}
