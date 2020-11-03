package io.github.asseco.pst.infrastructure.commands.mixins


import io.github.asseco.pst.Main
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.apache.log4j.Level
import org.slf4j.Logger
import picocli.CommandLine.Option

final class VerboseMixin {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)
    boolean[] verbosity;

    @Option(names = ["-v", "--verbose"], description = ["Increase verbosity. Specify multiple times to increase (-vvv)."])
    void setVerbosity(boolean[] aVerbosity) {
        this.verbosity = aVerbosity

        if (aVerbosity.length == 0 || aVerbosity == null) {
            LoggerFactory.setLogLevel(Level.WARN)
        }

        if (aVerbosity.length == 1) {
            LoggerFactory.setLogLevel(Level.INFO)
        }

        if (aVerbosity.length == 2) {
            LoggerFactory.setLogLevel(Level.DEBUG)
        }

        if (aVerbosity.length == 3) {
            LoggerFactory.setLogLevel(Level.TRACE)
        }

        if (aVerbosity.length >= 4) {
            LoggerFactory.setLogLevel(Level.ALL)
        }
    }

    boolean[] getVerbosity() {
        return this.verbosity
    }
}
