package io.github.asseco.pst.commands

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Help
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.HelpCommand

@Command(subcommands = [CalculateCommand.class, ValidateCommand.class, HelpCommand.class])
class BaseCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BaseCommand.class)

    static void main(String[] args) {
        LoggerFactory.setLoggerToConsole()
        int exitCode = 0

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
