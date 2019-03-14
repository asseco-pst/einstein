package com.pst.asseco.channels.devops.infrastructure.crawlers


import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.http.RepoExplorerFactory
import com.pst.asseco.channels.devops.infrastructure.DependencyParser
import com.pst.asseco.channels.devops.infrastructure.Einstein
import com.pst.asseco.channels.devops.infrastructure.Project
import com.pst.asseco.channels.devops.infrastructure.version.Version

class VersionSeekerMinion extends Worker {

    private final Project project
    private final String dependencyRecord

    VersionSeekerMinion(Project aProject, String aDependencyLine) {
        super()

        project = aProject
        dependencyRecord = aDependencyLine
        setId("$project.name:${project.version.toString()}<>$dependencyRecord")
    }

    @Override
    protected void work() {

        parseDependency()
    }

    private void parseDependency() {

        Console.info("Project '$project.ref' - Parsing dependency record '$dependencyRecord'")

        DependencyParser dependencyParser = getDependencyParser(dependencyRecord)
        String dependencyProjectName = dependencyParser.getProjectName()
        String dependencyVersion

        try {
            dependencyVersion = (dependencyParser.getVersionWrapper().isRcTag()) ? dependencyParser.getReadVersion() : getSiblingVersion(dependencyParser.getProjectNamespace(), dependencyProjectName, dependencyParser.getVersionWrapper())
        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${dependencyRecord}' of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        if (dependencyVersion) {
            Console.info("Project '$project.ref' - Dependency identified: $dependencyProjectName:$dependencyVersion")

            Project dependantProject = Project.factory(dependencyParser.getProjectNamespace(), dependencyProjectName, dependencyVersion)
            project.getDependencies().add(dependantProject)

            Einstein.getProjectsManager().calcDependencies(dependantProject, this)
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

        List<String> matchingTags = RepoExplorerFactory.get().listTags(aProjectNamespace, aProjectName,
                { tag -> aVersion.matchesVersion(tag.getName()) })

        if (!matchingTags)
            throw new Exception("Unable to get sibling version for $aProjectName:${aVersion.getVersionStr()}")

        return (matchingTags.size() == 1) ? matchingTags[0] : Version.getBiggestVersion(Version.factory(matchingTags))
    }
}
