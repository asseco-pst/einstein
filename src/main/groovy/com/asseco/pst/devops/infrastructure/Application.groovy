package com.asseco.pst.devops.infrastructure


@Singleton
class Application {

    List<String> dependencies = []
    Map dependenciesByProject = [:]
    List<Project> currently = []

    void calcDependencies(List<Project> aProjects) {

        aProjects.each { project ->
            DependenciesCrawlersManager.calcDependencies(project)
        }

        DependenciesCrawlersManager.waitUntilFinish()
        DependenciesCrawlersManager.interruptAll()
    }
}
