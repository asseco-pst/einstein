package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.utils.SemanticVersion
import io.github.asseco.pst.infrastructure.Requirement

class VersionSeekerMinion extends Worker {

    private final Project project
    private final Requirement requirement

    VersionSeekerMinion(Project aProject, Requirement aRequirement) {
        super()
        project = aProject
        requirement = aRequirement
        setId("$project.name:${project.version.toString()}<>${requirement}")
    }

    @Override
    protected void work() {
        seekVersion()
    }

    private void seekVersion() {

        Console.info("Project '$project.ref' - Parsing dependency record '$requirement'")

        String dependencyVersion
        try {
            dependencyVersion = SemanticVersion.findSatisfyingVersion(requirement)
        } catch (e) {
            Console.err("Unable to get sibling version for dependency record '${requirement}' of Project '${project.name}'. Cause: ${e}")
            throw e
        }

        if (dependencyVersion) {
            Console.info("Project '$project.ref' depends from ${requirement.getProjectName()}: ${dependencyVersion}")

            Project dependantProject = Project.factory(requirement.getProjectNamespace(), requirement.getProjectName(), dependencyVersion)
            project.addDependency(dependantProject)

            Einstein.getProjectsManager().calcDependencies(dependantProject, this)
        }
    }
}
