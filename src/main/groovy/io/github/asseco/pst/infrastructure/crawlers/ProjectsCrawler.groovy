package io.github.asseco.pst.infrastructure.crawlers


import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.utils.Console

class ProjectsCrawler extends Worker {

    ProjectsCrawler(DependenciesHandler aDepsHandler) {
        super(aDepsHandler)
        setId("projects.crawler")
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
