package com.asseco.pst.devops.infrastructure


@Singleton
class Application {

    void calcDependencies(List<Project> aProjects) {

        aProjects.each { project ->
            DependenciesCrawlersManager.calcDependencies(project)
        }

//        DependenciesCrawlersManager.waitUntilFinish()
        DependenciesCrawlersManager.interruptAll()
    }
}
