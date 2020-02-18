package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.App
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.utils.Console

class ProjectsCrawler extends Worker {

    List<Project> projects

    ProjectsCrawler(List<Project> aProjects) {
        super()
        _id = "projects.crawler"
        projects = aProjects
    }

    @Override
    void work() {
        calcDependencies()
    }

    private void calcDependencies() {

        projects.each { project ->

            App.einstein().addScannedProject(project)

            Console.debug("Calculating dependencies of Project '$project.name'")
            App.einstein().getProjectsManager().calcDependencies(project, this)
        }
    }
}
