package com.assecopst.channels.devops.infrastructure.cli

import com.assecopst.channels.devops.infrastructure.ProjectDAO
import groovy.cli.Option

class RDMCliOptions {

    // --help or -h
    @Option(shortName = 'h', description = 'display usage')
    Boolean help


    private List<ProjectDAO> projects

    // --projects or -p
    @Option(shortName = 'p', description = 'all projects for which runtime dependencies must be calculated. \nex: `-p server:2.0.0,backofficews:1.7.10`')
    void setProjects(String aProjects) {

        String splitter = ":" // character that splits the project name from its version
        projects = aProjects.tokenize(",").stream().collect({ p -> new ProjectDAO(name: p.split(splitter)[0], version: p.split(splitter)[1]) })
    }

    List<ProjectDAO> getProjects() { projects }


    // --saveToFile or -o
    private String saveToFile

    @Option(shortName = 'o', description = 'save results into a provided file. \nex: `-o \"C:\\tmp\\runtime-dependencies.txt\"')
    void setSaveToFile(String aSaveToFile) {
        saveToFile = aSaveToFile.toString().trim()
    }

    String getSaveToFile() { saveToFile }
}
