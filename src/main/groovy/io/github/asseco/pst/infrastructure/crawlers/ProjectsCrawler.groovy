package io.github.asseco.pst.infrastructure.crawlers


import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.utils.Console

class ProjectsCrawler extends Worker {

    private DependenciesHandler depsHandler

    ProjectsCrawler(DependenciesHandler aDepsHandler) {
        super()
        depsHandler = aDepsHandler
        _id = "projects.crawler"
    }

    @Override
    void work() {
        calcDependencies()
    }

    private void calcDependencies() {

        depsHandler.getProjects().each { project ->

            depsHandler.addScannedProject(project)

            Console.debug("Calculating dependencies of Project '$project.name'")
            depsHandler.calcDependencies(project, this)
        }
    }
}
