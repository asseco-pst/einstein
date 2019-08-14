package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.utils.Console

class FileParserMinion extends Crawler {


    FileParserMinion(Project aProject) {
        super(aProject)
    }

    @Override
    void work() {
        checkProjectDependencies()
        Einstein.addScannedProject(project)
    }

    private void checkProjectDependencies() {

        Console.print("Project '$project.ref' - Checking dependencies...")

        if (project.hasRequirementsFile()) {
            if (Einstein.isDebugModeOn())
                storeFile()
            parseRequirements()
        } else {
            Console.warn("Project '$project.ref' doesn't have a ${Project.REQUIREMENTS_FILE} file...")
        }
    }

    private void parseRequirements() {
        project.readRequirements().each { requirement ->
            MinionsFactory.create(MinionsFactory.Type.VERSION_SEEKER, project, this, requirement)
        }
    }
}
