package com.pst.asseco.channels.devops.infrastructure.crawlers

import com.pst.asseco.channels.devops.http.RepoExplorerFactory
import com.pst.asseco.channels.devops.infrastructure.Einstein
import com.pst.asseco.channels.devops.infrastructure.Project
import com.pst.asseco.channels.devops.infrastructure.Requirement
import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.vdurmont.semver4j.Semver

class VersionSeekerMinion extends Worker {

    private final Project project
    private final Requirement requirement

    VersionSeekerMinion(Project aProject, Requirement requirement) {
        super()
        project = aProject
        this.requirement = requirement
        setId("$project.name:${project.version.toString()}<>${this.requirement}")
    }

    @Override
    protected void work() {
        seekVersion()
    }

    private void seekVersion() {

        Console.info("Project '$project.ref' - Parsing dependency record '$requirement'")

        String dependencyVersion
        try {
            dependencyVersion = (requirement.isReleaseCandidate()) ? requirement.getRange() : findSatisfyingVersion(requirement)
        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${requirement}' of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        if (dependencyVersion) {
            Console.info("Project '$project.ref' - Dependency identified: ${requirement.toString()}")

            Project dependantProject = Project.factory(requirement.getNamespace(), requirement.getName(), dependencyVersion)
            project.addDependency(dependantProject)

            Einstein.getProjectsManager().calcDependencies(dependantProject, this)
        }
    }

    /**
     * Finds a tag in the required project that satisfies the requirement range.
     *
     * It does so by executing the following steps:
     *      * gets all tags from the project that satisfy the requirement range
     *      * returns the highest version that satisfies the range
     *
     * @param requirement
     * @return the version value
     */
    private static String findSatisfyingVersion(Requirement requirement) {

        List<String> tags = RepoExplorerFactory.get().listTags(
                requirement.getNamespace(),
                requirement.getName(),
                { tag ->
                    Semver version = new Semver(tag.getName(), Semver.SemverType.NPM)
                    version.satisfies(requirement.getRange())
                })

        Semver satisfies = tags.collect { new Semver(it, Semver.SemverType.NPM) }.max{ a, b ->
            a <=> b ?: a.isLowerThan(b)
        }

        return satisfies.getValue()

    }
}
