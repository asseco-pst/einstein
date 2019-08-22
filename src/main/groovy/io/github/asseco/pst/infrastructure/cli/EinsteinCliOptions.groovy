package io.github.asseco.pst.infrastructure.cli

import groovy.cli.Option
import io.github.asseco.pst.infrastructure.ProjectDao

class EinsteinCliOptions {

    // --help or -h
    @Option(shortName = 'h', description = 'display usage')
    Boolean help

    @Option(shortName = 'v', description = 'Provide additional details')
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
