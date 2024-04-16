package io.github.asseco.pst.commands


import io.github.asseco.pst.infrastructure.configproperties.properties.PropertiesLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Help
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.HelpCommand

@Command(subcommands = [CalculateCommand.class, ValidateCommand.class, HelpCommand.class])
class BaseCommand implements Runnable {

    static Logger logger

    static void main(String[] args) {
        int exitCode = 0
        PropertiesLoader.getLogBackProperties(args[0])
        logger = LoggerFactory.getLogger(BaseCommand.class)

        try {
            exitCode = new CommandLine(new BaseCommand())
                    .setColorScheme(Help.defaultColorScheme(Ansi.AUTO))
                    .execute(args)

            System.exit(exitCode)

        } catch (Exception exception) {
            logger.error("Error executing einstein due to:${exception.getMessage()}")
            System.exit(exitCode)
        }
    }

    @Override
    void run() {
        CommandLine.usage(new BaseCommand(), System.out, Ansi.AUTO)
        System.exit(0)
    }
}
