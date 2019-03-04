package com.assecopst.channels.devops.infrastructure.cli

import com.assecopst.channels.devops.infrastructure.ProjectDao
import groovy.cli.Option

class EinsteinCliOptions {

    // --help or -h
    @Option(shortName = 'h', description = 'display usage')
    Boolean help

    @Option(shortName = 'verbose', description = 'Show additional information about the dependencies calculation process')
    boolean verbose

    // --projects or -p
    private List<ProjectDao> projects

    @Option(shortName = 'p', description = 'all projects for which runtime dependencies must be calculated. \nex: `-p server:2.0.0,backofficews:1.7.10`')
    void setProjects(String aProjects) {

        String splitter = ":" // character that splits the project name from its version
        projects = aProjects.tokenize(",").stream().collect({ p -> new ProjectDao(name: p.split(splitter)[0], version: p.split(splitter)[1]) })
    }

    List<ProjectDao> getProjects() { projects }


    // --saveToFile or -o
    private String saveToFile

    @Option(shortName = 'o', description = 'save results into a provided file. \nex: `-o \"C:\\tmp\\runtime-dependencies.txt\"')
    void setSaveToFile(String aSaveToFile) {
        saveToFile = aSaveToFile.toString().trim()
    }

    String getSaveToFile() { saveToFile }
}
