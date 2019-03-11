package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.cli.CliParser
import com.assecopst.channels.devops.infrastructure.crawlers.ProjectsCrawler
import com.assecopst.channels.devops.infrastructure.metrics.Metrics
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.utils.EinsteinProperties

abstract class Einstein {

    static CliParser cli

    static Metrics metrics = new Metrics()
    static DependenciesManager dpManager = new DependenciesManager()
    static EinsteinProperties properties = new EinsteinProperties()
    static ProjectsManager projectsManager = new ProjectsManager()
    static synchronized List<Project> scannedDependencies = []
    static synchronized List<String> describedDependencies = []


    static CliParser getCli() {

        if (!cli)
            cli = new CliParser()
        return cli
    }

    static boolean isDebugModeOn() {

        if (cli) {
            if (cli.einsteinOptions.verbose)
                return true
        }

        return properties.isDebugModeOn()
    }

    static void calcDependencies(List<ProjectDao> aProjectsData) {

        Console.debug("Start tracking timer...")
        metrics.startTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)

        ProjectsCrawler pCrawler = new ProjectsCrawler(loadProjects(aProjectsData))
        pCrawler.start()
        pCrawler.join()

        Console.debug("Stop tracking timer...")
        metrics.stopTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)

//        Console.print("Check raw dependencies:")
//        Console.print(dpManager.getReadDependencies())

        dpManager.resolveVersions(scannedDependencies)

        Console.print("Check cleaned dependencies:")
        Console.print(dpManager.getFinalDependencies())

        Console.print("Described dependencies:")
        describedDependencies.sort().each { d ->
            Console.info(d)
        }

        Console.info("Einstein took " +
                metrics.getTimeDuration(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION).toString() +
                " calculating required dependencies")
    }

    private static List<Project> loadProjects(List<ProjectDao> aProjectsData) {

        List<Project> projects = []
        aProjectsData.each {
            projects << Project.factory(it.namespace, it.name, it.version)
        }

        return projects
    }
}
