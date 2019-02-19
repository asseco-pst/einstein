package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.crawlers.CrawlersManager
import com.assecopst.channels.devops.infrastructure.utils.Console

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
}
