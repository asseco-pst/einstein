package io.github.asseco.pst.commands.mixins


import io.github.asseco.pst.commands.logs.LoggerFactory
import org.apache.log4j.Level
import picocli.CommandLine.Option

final class VerboseMixin {
    boolean[] verbosity = []

    @Option(names = ["-v", "--verbose"], description = ["(OPTIONAL) Increase verbosity. Specify multiple times to increase (-vvv)."])
    void setVerbosity(boolean[] aVerbosity) {
        this.verbosity = aVerbosity

        if (aVerbosity.length == 0 || aVerbosity == null) {
            LoggerFactory.setLogLevel(Level.INFO)
        }

        if (aVerbosity.length == 1) {
            LoggerFactory.setLogLevel(Level.DEBUG)
        }

        if (aVerbosity.length == 2) {
            LoggerFactory.setLogLevel(Level.TRACE)
        }

        if (aVerbosity.length >= 3) {
            LoggerFactory.setLogLevel(Level.ALL)
        }
    }
}
