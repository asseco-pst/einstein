package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.http.RepoExplorerFactory
import com.assecopst.channels.devops.infrastructure.utils.Console

class Project {

    static final String REQUIREMENTS_FILE = "requirements.txt"

    String name
    String namespace
    String version
    String versionCommitSha
    String repoSshUrl
    String repoHttpsUrl
    String requirementsFileContent

    private Project() {}

    static Project factory(String aNamespace, String aName, String aVersion) {

        Project project

        try {
            project =
                    new Project.Builder()
                            .setNamespace(aNamespace)
                            .setName(aName)
                            .setVersion(aVersion)
                            .build()
        } catch (e) {
            Console.err("Unable to instantiate Project with name '${aName}' and version '${aVersion}'")
            throw e
        }

        return project
    }

    void loadRequirementsFileContent() {

        try {

            requirementsFileContent = RepoExplorerFactory.get().getFileContents(REQUIREMENTS_FILE, this.versionCommitSha,
                    this.namespace, this.name)

        } catch (e) {
            Console.err("Unable to load ${REQUIREMENTS_FILE} file content of ${name} Project")
            throw e
        }
    }

    boolean hasRequirementsFile() {
        return requirementsFileContent
    }

//    void parseRequirements() {
//
//        requirementsFileContent.eachLine { dependencyLine ->
//            String line = dependencyLine.trim()
//            if (!line)
//                return
//
//            parseDependency(line)
//        }
//    }

//    private void parseDependency(String aDependencyRecord) {
//
//        RecordParser recordParser = getRecordParser(aDependencyRecord)
//
//        Einstein.getDescribedDependencies().add("Project '${name}' requires Project '${recordParser.projectName}' on version ${recordParser.versionWrapper.versionStr}")
//
//        String dependencyProjectName = recordParser.getProjectName()
//
//        String dependencyVersion
//        try {
//            dependencyVersion = getSiblingVersion(dependencyProjectName, recordParser.getVersionWrapper())
//
//        } catch (e) {
//            Console.err("Unable to get sibling version for dependency record '${aDependencyRecord}' of Project '${name}'. Cause: ${e}")
//            throw e
//        }
//
//        if (dependencyVersion) {
//            Einstein.getDpManager().addDependency(dependencyProjectName, dependencyVersion)
//            MinionsFactory.calcDependencies(factory(dependencyProjectName, dependencyVersion))
//        }
//    }

//    private RecordParser getRecordParser(String aDependencyRecord) {
//
//        RecordParser recordParser
//
//        try {
//            recordParser = new RecordParser(aDependencyRecord)
//        } catch (e) {
//            Console.err("Unable to parse line '${aDependencyRecord}' from '${requirementsFilename}' file of Project '${name}'. Cause: ${e}")
//            throw e
//        }
//
//        return recordParser
//    }

//    private String getSiblingVersion(String aProjectName, Version aVersion) {
//
//        DB.Repos projectDB = (aProjectName.trim().toUpperCase() as DB.Repos)
//
//        List<String> tags = GitUtils.getTags(projectDB.sshUrl, aVersion.getVersionGitRegexExp())
//        List<String> matchingTags = tags.stream().filter({ line -> aVersion.matchesVersion(line) }).collect({ line -> aVersion.getTagFromExp(line) })
//
//        return (matchingTags.size() == 1) ? matchingTags[0] : Version.getBiggestVersion(matchingTags)
//    }

    static class Builder {

        private Project project

        Builder() {
            project = new Project()
        }

        Builder setNamespace(String namespace) {
            project.namespace = namespace
            return this
        }

        Builder setName(String aName) {
            project.name = aName
            return this
        }

        Builder setVersion(String aVersion) {
            project.version = aVersion
            return this
        }

        Project build() {

            project.setRepoSshUrl(RepoExplorerFactory.get().getRepoSshUrl(project.namespace, project.name))
            project.setRepoHttpsUrl(RepoExplorerFactory.get().getRepoWebUrl(project.namespace, project.name))
            project.versionCommitSha = RepoExplorerFactory.get().getTagHash(project.version, project.namespace, project.name)

            project.loadRequirementsFileContent()

            return project
        }
    }
}
