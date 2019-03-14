package com.pst.asseco.channels.devops.infrastructure.cli


import groovy.cli.picocli.CliBuilder

import java.nio.file.InvalidPathException
import java.nio.file.Paths

class CliParser {

    CliBuilder cli
    EinsteinCliOptions einsteinOptions

    CliParser() {
        cli = new CliBuilder(usage: "Calculate the runtime dependencies of provided Gitlab Projects")
    }


    void parse(String[] args) {
        try {

            einsteinOptions = cli.parseFromInstance(new EinsteinCliOptions(), args)
            checkProvidedOptions()

        } catch (e) {
            throw new Exception("An error occurred when trying to parse the arguments. Cause: ${e}")
        }
    }

    private void checkProvidedOptions() {

        if (einsteinOptions.help) {
            cli.usage()
            System.exit(0)
        }

        if (!einsteinOptions.projects)
            throw new Exception("No projects were provided")

        if (einsteinOptions.saveToFile) {
            if (!isValidPath())
                throw new Exception("--saveToFile parameter contains an invalid Path. Please, provide a valid one")
        }
    }

    private boolean isValidPath() {

        try {
            Paths.get(einsteinOptions.saveToFile)
        } catch (InvalidPathException | NullPointerException e) {
            return false
        }
        return true
    }
}
