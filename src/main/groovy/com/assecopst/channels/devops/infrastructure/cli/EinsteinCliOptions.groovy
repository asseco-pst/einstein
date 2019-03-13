package com.assecopst.channels.devops.infrastructure.cli

import com.assecopst.channels.devops.infrastructure.ProjectDao
import groovy.cli.Option

class EinsteinCliOptions {

    // --help or -h
    @Option(shortName = 'h', description = 'display usage')
    Boolean help

    @Option(shortName = 'verbose', description = 'Show additional information along the dependencies calculation process')
    boolean verbose

    // --projects or -p
    private List<ProjectDao> projects

    @Option(shortName = 'p', description = 'all projects for which runtime dependencies must be calculated. \nex: `-p middleware/server:2.0.0,middleware/backofficews:1.7.10`')
    void setProjects(String aProjects) {

        projects = aProjects.tokenize(",").stream().collect({ p ->
            ProjectDao.fromFullName(p)
        })

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
