package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.DB
import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.RecordParser
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.utils.GitUtils
import com.assecopst.channels.devops.infrastructure.version.Version

class Minion {

    Project project
    File workspaceFolder

    Minion(Project aProject, File aWorkspaceFolder) {
        project = aProject
        workspaceFolder = aWorkspaceFolder
    }


    void checkDependencies() {

        Console.print("Checking dependencies for Project '${project.name}'")

        project.loadRequirementsFileContent()

        if (project.hasRequirementsFile()) {
            storeFile() // store file for further analysis
            parseRequirements()
        } else {
            Console.warn("Project ${project.name} doesn't have a requirements file...")
        }
    }

    private void storeFile() {

        Thread thread = new Thread(new FileStorer(project, workspaceFolder))
        thread.start()
    }

    private void parseRequirements() {

        project.requirementsFileContent.eachLine { reqLine ->
            String line = reqLine.trim()
            if (!line)
                return

            parseDependency(line)
        }
    }

    private void parseDependency(String aDependencyRecord) {

        RecordParser recordParser = getRecordParser(aDependencyRecord)
        String dependencyProjectName = recordParser.getProjectName()

        String dependencyVersion
        try {
            dependencyVersion = getSiblingVersion(dependencyProjectName, recordParser.getVersionWrapper())

        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${aDependencyRecord}' of Project '${project.getName()}'. Cause: ${e}")
        }

        if (dependencyVersion) {
            DB.Repos projectDB = dependencyProjectName.toUpperCase() as DB.Repos
            CrawlersManager.calcDependencies(new Project(dependencyProjectName, dependencyVersion, projectDB.httpsUrl, projectDB.sshUrl))
        }
    }

    private RecordParser getRecordParser(String aDependencyRecord) {

        RecordParser recordParser

        try {
            recordParser = new RecordParser(aDependencyRecord)
        } catch (e) {
            Console.err("Unable to parse line '${aDependencyRecord}' from '${Project.requirementsFilename}' file of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        return recordParser
    }

    private String getSiblingVersion(String aProjectName, Version aVersion) {

        DB.Repos projectDB = (aProjectName.trim().toUpperCase() as DB.Repos)

        List<String> tags = GitUtils.getTags(projectDB.sshUrl, aVersion.getVersionGitRegexExp())
        List<String> matchingTags = tags.stream().filter({ line -> aVersion.matchesVersion(line) }).collect()

        return matchingTags.reverse()[0] // it returns the biggest/newest sibling version
    }
}
