package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.Einstein
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

            Einstein.addScannedProject(project)

            Console.debug("Calculating dependencies of Project '$project.name'")
            Einstein.getProjectsManager().calcDependencies(project, this)
        }
    }
}
