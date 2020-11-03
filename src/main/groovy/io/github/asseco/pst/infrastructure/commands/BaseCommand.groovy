package io.github.asseco.pst.infrastructure.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand

@Command(subcommands = [CalculateCommand.class, ValidateCommand.class, HelpCommand.class])
class BaseCommand implements Runnable {
    static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new BaseCommand())

        try {
            commandLine.execute(args)

        } catch (Exception ignore) {
            println("Error executing tool cause:" + ignore.getMessage())
            commandLine.usage(System.out);

            System.exit(1)
        }
    }

    @Override
    void run() {
        (new CommandLine(new BaseCommand())).usage(System.out);
        System.exit(0)
    }
}
