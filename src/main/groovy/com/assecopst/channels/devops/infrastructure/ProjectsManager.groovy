package com.assecopst.channels.devops.infrastructure


import com.assecopst.channels.devops.infrastructure.crawlers.MinionsFactory
import com.assecopst.channels.devops.infrastructure.crawlers.Worker
import com.assecopst.channels.devops.infrastructure.utils.Console

class ProjectsManager {

    private synchronized List<String> crawledProjects

    ProjectsManager() {
        crawledProjects = []
    }

    void calcDependencies(Project aProject, Worker aObserver) {

        if (isAlreadyCrawled(aProject))
            return
        addCrawledProject(aProject)

        Console.debug("Launching FileParserMinion to calculate dependencies of Project $aProject.name:$aProject.version")
        MinionsFactory.create(MinionsFactory.Type.CRAWLER, aProject, aObserver)
    }

    private boolean isAlreadyCrawled(Project aProject) {

        String projectId = aProject.name
        if (crawledProjects.contains(projectId)) {
            Console.debug("'${projectId}' Project was already crawled...")
            return true
        }
        crawledProjects << projectId

        return false
    }

    private void addCrawledProject(Project aProject) {
        crawledProjects << aProject.name
        // TODO: check if save only the project's name is enough...
    }
}
