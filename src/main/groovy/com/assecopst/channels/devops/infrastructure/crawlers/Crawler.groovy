package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.utils.Console

class Crawler extends Worker {


    Crawler(Project aProject) {
        super(aProject)
    }

    @Override
    void run() {
        checkDependencies()
    }


    private void checkDependencies() {

        Console.print("Checking dependencies for Project '${project.name}'")

        if (project.hasRequirementsFile()) {
            storeFile() // store file for debug purposes
            project.parseRequirements()
        } else {
            Console.warn("Project ${project.name} doesn't have a requirements file...")
        }
    }

    private void storeFile() {

        Thread thread = new Thread(new FileStorer(project, workspaceFolder))
        thread.start()
    }
}
