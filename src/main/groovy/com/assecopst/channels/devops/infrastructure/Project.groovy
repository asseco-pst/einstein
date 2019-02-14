package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.crawlers.CrawlersManager
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.utils.GitUtils
import com.assecopst.channels.devops.infrastructure.utils.GitlabUtils
import com.assecopst.channels.devops.infrastructure.version.Version

class Project {

    static final String requirementsFilename = "requirements.txt"

    String name
    String version
    String versionCommitSha
    String repoSshUrl
    String repoHttpsUrl
    String requirementsFileContent

    private Project() {}

    static Project factory(String aName, String aVersion) {

        DB.Repos projectRepo
        try {
            projectRepo = aName.toUpperCase() as DB.Repos
        } catch (e) {
            Console.err("Unable to find project with name '${aName}'.")
            throw e
        }

        Project project

        try {
            project =
                    new Project.Builder()
                            .setName(aName)
                            .setVersion(aVersion)
                            .setRepoHttpsUrl(projectRepo.httpsUrl)
                            .setRepoSshUrl(projectRepo.sshUrl)
                            .build()
        } catch (e) {
            Console.err("Unable to instantiate Project with name '${aName}' and version '${aVersion}'")
            throw e
        }


        return project
    }

    void loadRequirementsFileContent() {

        try {
            requirementsFileContent = GitlabUtils.getFileContentFromRepo(this)
        } catch (e) {
            Console.err("Unable to load ${requirementsFilename} file content of ${name} Project")
            throw e
        }
    }

    boolean hasRequirementsFile() {
        return requirementsFileContent
    }

    void parseRequirements() {

        requirementsFileContent.eachLine { dependencyLine ->
            String line = dependencyLine.trim()
            if (!line)
                return

            parseDependency(line)
        }
    }

    private void parseDependency(String aDependencyRecord) {

        RecordParser recordParser = getRecordParser(aDependencyRecord)

        Einstein.getDescribedDependencies().add("Project '${name}' requires Project '${recordParser.projectName}' on version ${recordParser.versionWrapper.versionStr}")

        String dependencyProjectName = recordParser.getProjectName()

        String dependencyVersion
        try {
            dependencyVersion = getSiblingVersion(dependencyProjectName, recordParser.getVersionWrapper())

        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${aDependencyRecord}' of Project '${name}'. Cause: ${e}")
        }

        if (dependencyVersion) {
            Einstein.getDpManager().addDependency(dependencyProjectName, dependencyVersion)
            calcProjectDependencies(dependencyProjectName, dependencyVersion)
        }
    }

    private RecordParser getRecordParser(String aDependencyRecord) {

        RecordParser recordParser

        try {
            recordParser = new RecordParser(aDependencyRecord)
        } catch (e) {
            Console.err("Unable to parse line '${aDependencyRecord}' from '${requirementsFilename}' file of Project '${name}'. Cause: ${e}")
            throw e
        }

        return recordParser
    }

    private String getSiblingVersion(String aProjectName, Version aVersion) {

        DB.Repos projectDB = (aProjectName.trim().toUpperCase() as DB.Repos)

        List<String> tags = GitUtils.getTags(projectDB.sshUrl, aVersion.getVersionGitRegexExp())
        List<String> matchingTags = tags.stream().filter({ line -> aVersion.matchesVersion(line) }).collect({ line -> aVersion.getTagFromExp(line) })

        return (matchingTags.size() == 1) ? matchingTags[0] : Version.getBiggestVersion(matchingTags)
    }

    private void calcProjectDependencies(String aDependencyProjectName, String aVersion) {

        DB.Repos projectDB = aDependencyProjectName.toUpperCase() as DB.Repos

        CrawlersManager.calcDependencies(factory(aDependencyProjectName, aVersion))
    }


    static class Builder {

        private Project project

        Builder() {
            project = new Project()
        }

        Builder setName(String aName) {
            project.name = aName
            return this
        }

        Builder setVersion(String aVersion) {
            project.version = aVersion
            return this
        }

        Builder setRepoSshUrl(String aRepoSshUrl) {
            project.repoSshUrl = aRepoSshUrl
            return this
        }

        Builder setRepoHttpsUrl(String aRepoHttpsUrl) {
            project.repoHttpsUrl = aRepoHttpsUrl
            return this
        }

        Project build() {

            project.versionCommitSha = GitUtils.getTagCommitSha(project.repoSshUrl, project.version)
            project.loadRequirementsFileContent()

            return project
        }
    }
}
