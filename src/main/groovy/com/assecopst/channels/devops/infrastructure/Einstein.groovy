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

class Einstein {

    static DependenciesManager dpManager = new DependenciesManager()
    static List<String> describedDependencies = []


    static void calcDependencies(List<ProjectDao> aProjectsData) {

        loadProjects(aProjectsData).each { project ->
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

    private static List<Project> loadProjects(List<ProjectDao> aProjectsData) {

        List<Project> projects = []
        aProjectsData.each {
            ProjectDao pData = (ProjectDao) it
            projects << Project.factory(pData.name, pData.version)
        }

        return projects
    }

    static Map getResults() {
        return dpManager.getFinalDependencies()
    }
}
