package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FileParserMinion extends Crawler {
    private static final Logger logger = LoggerFactory.getLogger(FileParserMinion.class)

    FileParserMinion(DependenciesHandler aDepsHandler, Project aProject) {
        super(aDepsHandler, aProject)
    }

    @Override
    void work() {
        checkProjectDependencies()
        depsHandler.addScannedProject(project)
    }

    private void checkProjectDependencies() {
        logger.info("Checking dependencies of Project '${project.ref}':")
        if (project.hasRequirementsFile()) {
            logger.debug("Parsing ${Project.EINSTEIN_FILENAME} file for project '${project.ref}'.")
            parseRequirements()
        } else {
            logger.warn("Project '${project.ref}' doesn't have a ${Project.EINSTEIN_FILENAME} file...")
        }
    }

    private void parseRequirements() {
        project.readRequirements().each { requirement ->
            logger.debug("Parsing requirement ${requirement}")
            MinionsFactory.launch(MinionsFactory.Type.VERSION_SEEKER, project, this, depsHandler, requirement)
        }
    }
}
