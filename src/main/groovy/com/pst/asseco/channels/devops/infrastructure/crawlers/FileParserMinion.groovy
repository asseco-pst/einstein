package com.pst.asseco.channels.devops.infrastructure.crawlers


import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.Einstein
import com.pst.asseco.channels.devops.infrastructure.Project

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
            Console.warn("Project '$project.ref' doesn't have a requirements file...")
        }
    }

    private void parseRequirements() {

        project.getRequirementsFileContent().eachLine { dependencyLine ->
            String line = dependencyLine.trim()
            if (!line)
                return

            MinionsFactory.create(MinionsFactory.Type.VERSION_SEEKER, project, this, line)
        }
    }
}
