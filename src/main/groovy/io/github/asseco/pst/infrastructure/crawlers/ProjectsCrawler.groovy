package io.github.asseco.pst.infrastructure.crawlers


import io.github.asseco.pst.infrastructure.DependenciesHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProjectsCrawler extends Worker {
    private static final Logger logger = LoggerFactory.getLogger(ProjectsCrawler.class)
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
            logger.debug("Calculating dependencies for project '${project.name}'")
            depsHandler.calcDependencies(project, this)
        }
    }
}
