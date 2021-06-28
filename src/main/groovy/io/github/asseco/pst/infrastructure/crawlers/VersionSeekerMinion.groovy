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
        logger.info("Project '${project.ref}' - Parsing dependency requirement '${requirement}'")
        String dependencyVersion

        try {
            dependencyVersion = SemanticVersion.findSatisfyingVersion(requirement.projectNamespace, requirement.projectName, requirement.versionRange)
        } catch (exception) {
            logger.warn("Unable to get sibling version for dependency requirement '${requirement}' of project '${project.name}'. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
        }

        if (dependencyVersion) {
            logger.info("Project '${project.ref}' depends of ${requirement.getProjectName()}:${dependencyVersion}")
            Project dependantProject = Project.factory(requirement.getProjectNamespace(), requirement.getProjectName(), dependencyVersion, project.ref)

            project.addDependency(dependantProject)

            logger.debug("Calculating dependencies for requirement ${requirement}")
            depsHandler.calcDependencies(dependantProject, this)
        }
    }
}
