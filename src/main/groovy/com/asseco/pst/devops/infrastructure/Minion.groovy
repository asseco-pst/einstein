package com.asseco.pst.devops.infrastructure


import com.asseco.pst.devops.infrastructure.utils.Console
import com.asseco.pst.devops.infrastructure.utils.GitUtils
import com.asseco.pst.devops.infrastructure.version.Version


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

        if(project.hasRequirementsFile()) {
            storeFile() // store file for further analysis
            parseRequirements()
        } else {
            Console.warn("Project ${project.name} doesn't have a requirements file...")
        }
    }

    private void storeFile() {

        String filename = "${Project.requirementsFilename}-${project.versionCommitSha}"

        Console.print("Storing ${filename} for Project ${project.name}" )

        try {
            File projectFolder = new File(workspaceFolder, project.name)
            projectFolder.mkdir()

            File requirements = new File(projectFolder, filename)
            requirements.write(project.requirementsFileContent)
        } catch (e) {
            Console.err("Unable to store requirements file for Project '${project.name}'. Cause: ${e}")
        }
    }

    private void parseRequirements() {

        project.requirementsFileContent.eachLine { line ->
            line = line.trim()
            if(!line)
                return

            parseDependency(line)
        }
    }

    private void parseDependency(String aDependencyRecord) {

        DependencyRecordParser recordParser = getRecordParser(aDependencyRecord)

        String dependencyProjectName = recordParser.getProjectName()

        String dependencyVersion
        try {
            dependencyVersion =  getSiblingVersion(dependencyProjectName, recordParser.getVersionWrapper())

        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${aDependencyRecord}' of Project '${project.getName()}'. Cause: ${e}")
        }

        if(dependencyVersion) {
            DB.Repos projectDB = dependencyProjectName.toUpperCase() as DB.Repos
            DependenciesCrawlersManager.calcDependencies(new Project(dependencyProjectName, dependencyVersion, projectDB.httpsUrl, projectDB.sshUrl))
        }
    }

    private DependencyRecordParser getRecordParser(String aDependencyRecord){

        DependencyRecordParser recordParser

        try {
            recordParser = new DependencyRecordParser(aDependencyRecord)
        } catch (e) {
            Console.err("Unable to parse line '${aDependencyRecord}' from '${Project.requirementsFilename}' file of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        return recordParser
    }

    private String getSiblingVersion(String aProjectName, Version aVersion) {

        DB.Repos projectDB = (aProjectName.trim().toUpperCase() as DB.Repos)

        List<String> tags = GitUtils.getTags(projectDB.sshUrl, aVersion.getVersionGitRegexExp())
        List<String> matchingTags = tags.stream().filter({ line -> aVersion.matchesVersion(line)}).collect()

        return matchingTags.reverse()[0] // it returns the biggest/newest sibling version
    }
}
