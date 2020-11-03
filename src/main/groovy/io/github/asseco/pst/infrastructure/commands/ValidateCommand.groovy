package io.github.asseco.pst.infrastructure.commands

import io.github.asseco.pst.infrastructure.commands.mixins.LogOutputMixin
import io.github.asseco.pst.infrastructure.commands.mixins.SaveToFileMixin
import io.github.asseco.pst.infrastructure.commands.mixins.VerboseMixin
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Option

@Command(name = "validate", description = "This command allows validating the einstein dependencies for a project", subcommands = [HelpCommand.class])
class ValidateCommand implements Runnable {
    private String inputFile

    @CommandLine.Mixin
    private SaveToFileMixin saveToFileMixin
    @CommandLine.Mixin
    private LogOutputMixin logOutputMixin
    @CommandLine.Mixin
    private VerboseMixin verboseMixin

    @Option(names = ["-i", "--input"], description = "YAML file for which the dependencies must be validated")
    void setInputFile(String aInputFile) {
        this.inputFile = inputFile
    }

    @Override
    void run() {
    }
}
