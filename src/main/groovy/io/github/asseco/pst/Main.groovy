package io.github.asseco.pst

import groovy.json.JsonBuilder
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.cli.CliParser

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.PatternLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Main {

    static CliParser cliParser
    private static final Logger logger = LoggerFactory.getLogger(Main.class)

    static void main(String[] args) {

        cliParser = new CliParser()

        try {
            cliParser.parse(args)
        } catch (e) {
            logger.error(e.getMessage())
            cliParser.cli.usage()
            System.exit(1)
        }
        setLoggerToConsole()

        try {

            Map<String, String> parsedDeps = Einstein.instance.calcDependencies(cliParser.einsteinOptions.projects)
            String outputFilePath = cliParser.einsteinOptions.saveToFile
            if (outputFilePath)
                saveResultsIntoFile(parsedDeps, outputFilePath)

        } catch (e) {
            interrupt(e)
        }

        logger.print("Finished!")
    }

    static private void saveResultsIntoFile(Map<String, String> aParsedDeps, String aFilePath) {

        try {
            logger.info("Saving dependencies into file ${aFilePath}")
            new File(aFilePath).write(new JsonBuilder(aParsedDeps).toPrettyString())
        } catch (e) {
            logger.error("Unable to save results into output file '${aFilePath}'. Cause: ${e}")
            System.exit(1)
        }
    }
     private static void setLoggerToConsole() {
         org.apache.log4j.Logger.getRootLogger().removeAllAppenders()
         ConsoleAppender console = new ConsoleAppender() //create appender
         //configure the appender
         String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1} - %m%n"
         console.setLayout(new PatternLayout(PATTERN))
         console.activateOptions()
         //add appender to any Logger (here is root)
         org.apache.log4j.Logger.getRootLogger().addAppender(console)
         if (cliParser.einsteinOptions && cliParser.einsteinOptions.verbose)
             org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG)
         else
             org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO)

     }

    static void interrupt(Exception aException) {

        logger.error("An error occurred. Cause: ${aException}", aException)
        System.exit(1)
    }
}
