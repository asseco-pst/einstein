package io.github.asseco.pst.infrastructure


import io.github.asseco.pst.infrastructure.crawlers.MinionsFactory
import io.github.asseco.pst.infrastructure.crawlers.Worker
import io.github.asseco.pst.infrastructure.utils.Console

class ProjectsManager {

    private synchronized Set<String> crawledProjects

    ProjectsManager() {
        crawledProjects = []
    }

    void calcDependencies(Project aProject, Worker aObserver) {

        if (isAlreadyCrawled(aProject))
            return
        addCrawledProject(aProject)

        Console.debug("Launching FileParserMinion to calculate dependencies of Project $aProject.ref")
        MinionsFactory.create(MinionsFactory.Type.CRAWLER, aProject, aObserver)
    }

    private boolean isAlreadyCrawled(Project aProject) {

        String ref = aProject.ref
        if (crawledProjects.contains(ref)) {
            Console.debug("Project '${ref}' was already crawled...")
            return true
        }
        crawledProjects << ref

        return false
    }

    private void addCrawledProject(Project aProject) {
        crawledProjects << aProject.ref
    }
}
