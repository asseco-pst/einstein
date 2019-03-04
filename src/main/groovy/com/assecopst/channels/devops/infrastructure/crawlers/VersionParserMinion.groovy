package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.DB
import com.assecopst.channels.devops.infrastructure.Einstein
import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.RecordParser
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.utils.GitUtils
import com.assecopst.channels.devops.infrastructure.version.Version

class VersionParserMinion extends Worker {

    private final Project project
    private final String dependencyLine

    VersionParserMinion(Project aProject, String aDependencyLine) {
        super()

        project = aProject
        dependencyLine = aDependencyLine
        setId("$project.name:$project.version<>$dependencyLine")
    }

    @Override
    protected void work() {

        parseDependencyVersion()
    }

    private void parseDependencyVersion() {

        RecordParser recordParser = getRecordParser(dependencyLine)

        Einstein.getDescribedDependencies().add("Project '${project.name}' requires Project '${recordParser.projectName}' on version ${recordParser.versionWrapper.versionStr}")

        String dependencyProjectName = recordParser.getProjectName()

        String dependencyVersion
        try {
            dependencyVersion = getSiblingVersion(dependencyProjectName, recordParser.getVersionWrapper())

        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${dependencyLine}' of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        if (dependencyVersion) {
            Einstein.getDpManager().addDependency(dependencyProjectName, dependencyVersion)
            Einstein.getProjectsManager().calcDependencies(Project.factory(dependencyProjectName, dependencyVersion), this)
        }
    }

    private String getSiblingVersion(String aProjectName, Version aVersion) {

        DB.Repos projectDB = (aProjectName.trim().toUpperCase() as DB.Repos)

        List<String> tags = GitUtils.getTags(projectDB.sshUrl, aVersion.getVersionGitRegexExp())
        List<String> matchingTags = tags.stream().filter({ line -> aVersion.matchesVersion(line) }).collect({ line -> aVersion.getTagFromExp(line) })

        return (matchingTags.size() == 1) ? matchingTags[0] : Version.getBiggestVersion(matchingTags)
    }

    private RecordParser getRecordParser(String aDependencyRecord) {

        RecordParser recordParser

        try {
            recordParser = new RecordParser(aDependencyRecord)
        } catch (e) {
            Console.err("Unable to parse line '${aDependencyRecord}' from '${project.getRequirementsFilename()}' file of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        return recordParser
    }
}
