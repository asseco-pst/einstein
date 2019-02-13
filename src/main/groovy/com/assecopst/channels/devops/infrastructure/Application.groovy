package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.crawlers.CrawlersManager
import com.assecopst.channels.devops.infrastructure.utils.Console


/**
 * @TODO
 *  - DONE - Register the calculated dependencies
 *  - DONE - Check if - on the calculated dependencies - there exists duplicate modules with non-retro compatible versions
 *  - Prepare application to be invoked by command line
 *  - DONE - For projects which were identified more than 1 version, calculate the version that must be sent
 *  - Change syntax of dependencies declaration
 *  - Save the results as json object on given file
 */

class Application {

    static DependenciesManager dpManager = new DependenciesManager()
    static List<String> describedDependencies = []

    static void calcDependencies(List<Project> aProjects, boolean aSaveOnFile = false) {

        aProjects.each { project ->

            dpManager.addDependency(project.name, project.version)
            CrawlersManager.calcDependencies(project)
        }
        CrawlersManager.interruptAll()

        Console.print("Check raw dependencies:")
        Console.print(dpManager.getReadDependencies())

        dpManager.resolveVersions()

        Console.print("Check cleaned dependencies:")
        Console.print(dpManager.getFinalDependencies())

        Console.print("Described dependencies:")
        Console.print(describedDependencies.sort().join("\n"))
    }
}
