package com.pst.asseco.channels.devops

import com.pst.asseco.channels.devops.infrastructure.Einstein
import com.pst.asseco.channels.devops.infrastructure.cli.CliParser
import com.pst.asseco.channels.devops.infrastructure.utils.Console
import groovy.json.JsonBuilder

class Main {

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

            Einstein.calcDependencies(cliParser.einsteinOptions.projects)

            String outputFilePath = cliParser.einsteinOptions.saveToFile
            if (outputFilePath)
                saveResultsIntoFile(outputFilePath)

        } catch (e) {
            interrupt(e)
        }

        Console.print("Finished!")
    }

    static private void saveResultsIntoFile(String aFilePath) {

        try {
            Console.info("Saving dependencies into file ${aFilePath}")
            new File(aFilePath).write(new JsonBuilder(Einstein.getCalculatedDependencies()).toPrettyString())
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
