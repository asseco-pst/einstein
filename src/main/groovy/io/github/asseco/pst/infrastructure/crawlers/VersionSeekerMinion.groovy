package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.Requirement

import io.github.asseco.pst.infrastructure.utils.SemanticVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VersionSeekerMinion extends Worker {

    private static final Logger logger = LoggerFactory.getLogger(VersionSeekerMinion.class)
    private final Project project
    private final Requirement requirement

    VersionSeekerMinion(DependenciesHandler aDepsHandler, Project aProject, Requirement aRequirement) {
        super(aDepsHandler)
        project = aProject
        requirement = aRequirement
        setId("$project.name:${project.version.toString()}<>${requirement}")
    }

    @Override
    protected void work() {
        seekVersion()
    }

    private void seekVersion() {

        logger.info("Project '$project.ref' - Parsing dependency record '$requirement'")

        String dependencyVersion
        try {
            dependencyVersion = SemanticVersion.findSatisfyingVersion(requirement)
        } catch (e) {
            logger.error("Unable to get sibling version for dependency record '${requirement}' of Project '${project.name}'", e)
            throw e
        }

        if (dependencyVersion) {
            logger.info("Project '$project.ref' depends from ${requirement.getProjectName()}: ${dependencyVersion}")

            Project dependantProject = Project.factory(requirement.getProjectNamespace(), requirement.getProjectName(), dependencyVersion)
            project.addDependency(dependantProject)

            depsHandler.calcDependencies(dependantProject, this)
        }
    }
}
