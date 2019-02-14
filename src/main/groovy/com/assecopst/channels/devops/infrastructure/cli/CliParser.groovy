package com.assecopst.channels.devops.infrastructure.cli


import groovy.cli.picocli.CliBuilder

import java.nio.file.InvalidPathException
import java.nio.file.Paths

class CliParser {

    CliBuilder cli
    RDMCliOptions rdmOptions

    CliParser() {
        cli = new CliBuilder(usage: "Calculate the runtime dependencies of provided Gitlab Projects")
    }


    void parse(String[] args) {
        try {
            
            rdmOptions = cli.parseFromInstance(new RDMCliOptions(), args)
            checkProvidedOptions()

        } catch (e) {
            throw new Exception("An error occurred when trying to parse the arguments. Cause: ${e}")
        }
    }

    private void checkProvidedOptions() {

        if (rdmOptions.help) {
            cli.usage()
            System.exit(0)
        }

        if (!rdmOptions.projects)
            throw new Exception("No gitlab projects were provided")

        if (rdmOptions.saveToFile) {
            if (!isValidPath())
                throw new Exception("--saveToFile parameter contains an invalid Path. Please, provide a valid one")
        }
    }

    private boolean isValidPath() {

        try {
            Paths.get(rdmOptions.saveToFile)
        } catch (InvalidPathException | NullPointerException e) {
            return false
        }
        return true
    }
}
