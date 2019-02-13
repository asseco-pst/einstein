package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.crawlers.CrawlersManager


/**
 * @TODO
 *  - Register the calculated dependencies
 *  - Check if - on the calculated dependencies - there exists duplicate modules with non-retro compatible versions
 */

class Application {

    static DependenciesManager dpManager = new DependenciesManager()

    static void calcDependencies(List<Project> aProjects, boolean aSaveOnFile = false) {

        aProjects.each { project ->

            dpManager.addDependency(project.name, project.version)
            CrawlersManager.calcDependencies(project)
        }
        CrawlersManager.interruptAll()

        println dpManager.getDependencies()
        dpManager.checkVersionsCompatibility()

    }
}
