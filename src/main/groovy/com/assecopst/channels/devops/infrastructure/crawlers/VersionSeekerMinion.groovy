package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.http.RepoExplorerFactory
import com.assecopst.channels.devops.infrastructure.DependencyParser
import com.assecopst.channels.devops.infrastructure.Einstein
import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.version.Version
import org.gitlab4j.api.models.Tag

class VersionSeekerMinion extends Worker {

    private final Project project
    private final String dependencyRecord

    VersionSeekerMinion(Project aProject, String aDependencyLine) {
        super()

        project = aProject
        dependencyRecord = aDependencyLine
        setId("$project.name:$project.version<>$dependencyRecord")
    }

    @Override
    protected void work() {

        parseDependency()
    }

    private void parseDependency() {

        Console.info("Project '$project.name:$project.version' - Parsing dependency record '$dependencyRecord'")

        DependencyParser dependencyParser = getDependencyParser(dependencyRecord)

        Einstein.getDescribedDependencies().add("Project '$project.name:$project.version' requires Project $dependencyParser.projectName:$dependencyParser.versionWrapper.versionStr")

        String dependencyProjectName = dependencyParser.getProjectName()

        String dependencyVersion
        try {
            dependencyVersion = getSiblingVersion(dependencyParser.getProjectNamespace(), dependencyProjectName, dependencyParser.getVersionWrapper())
        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${dependencyRecord}' of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        if (dependencyVersion) {
            Console.info("Project '$project.name:$project.version' - Dependency identified: $dependencyProjectName:$dependencyVersion")
            Einstein.getDpManager().addDependency(dependencyProjectName, dependencyVersion)
            Einstein.getProjectsManager().calcDependencies(Project.factory(dependencyParser.getProjectNamespace(), dependencyProjectName, dependencyVersion), this)
        }
    }

    private DependencyParser getDependencyParser(String aDependencyRecord) {

        DependencyParser recordParser

        try {
            recordParser = new DependencyParser(aDependencyRecord)
        } catch (e) {
            Console.err("Unable to parse line '${aDependencyRecord}' from '${Project.REQUIREMENTS_FILE}' file of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        return recordParser
    }

    private String getSiblingVersion(String aProjectNamespace, String aProjectName, Version aVersion) {

        List<String> matchingTags = RepoExplorerFactory.get().listTags(aProjectNamespace, aProjectName
                , { tag -> aVersion.matchesVersion(tag.getName())})

        if (!matchingTags)
            throw new Exception("Unable to get sibling version for $aProjectName:${aVersion.getVersionStr()}")

        return (matchingTags.size() == 1) ? matchingTags[0] : Version.getBiggestVersion(matchingTags)
    }
}
