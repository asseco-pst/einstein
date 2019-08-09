package com.pst.asseco.channels.devops.infrastructure

import com.pst.asseco.channels.devops.http.RepoExplorerFactory
import com.pst.asseco.channels.devops.infrastructure.cli.CliParser
import com.pst.asseco.channels.devops.infrastructure.crawlers.ProjectsCrawler
import com.pst.asseco.channels.devops.infrastructure.metrics.Metrics
import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.utils.EinsteinProperties

import java.nio.file.Path
import java.nio.file.Paths

abstract class Einstein {

    static CliParser cli

    static Metrics metrics = new Metrics()
    static DependenciesManager dpManager = new DependenciesManager()
    static EinsteinProperties properties = new EinsteinProperties()
    static ProjectsManager projectsManager = new ProjectsManager()
    static synchronized List<Project> scannedDependencies = []
    

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

    static void addScannedProject(Project aProject) {

        if (!scannedDependencies.contains(aProject))
            scannedDependencies << aProject
    }

    static Path getWorkspaceFolder() {

        Path workspaceFolderPath = Paths.get([getUserHome(), properties.getWorkspaceRootFolder()].join("/"))

        File folder = new File(workspaceFolderPath.toString())
        if(!folder.exists())
            folder.mkdirs()

        return workspaceFolderPath

    }

    static void calcDependencies(List<ProjectDao> aProjectsData) {

        RepoExplorerFactory.create()

        metrics.startTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)

        ProjectsCrawler pCrawler = new ProjectsCrawler(loadProjects(aProjectsData))
        pCrawler.start()
        pCrawler.join()

        metrics.stopTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)

        dpManager.resolveVersions(scannedDependencies)

        Console.info("Calculated dependencies:")
        Console.print(dpManager.getCalcDependencies())

        Console.info("Einstein took " +
                metrics.getTimeDuration(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION).toString())
    }

    static Map getCalculatedDependencies() {
        return dpManager.getCalcDependencies()
    }

    private static List<Project> loadProjects(List<ProjectDao> aProjectsData) {

        List<Project> projects = []
        aProjectsData.each {
            projects << Project.factory(it.namespace, it.name, it.version)
        }

        return projects
    }

    private static String getUserHome() {

        String userHome = System.getenv("HOME")

        if(!userHome)
            userHome = System.getenv("USERPROFILE")

        if(!userHome)
            throw new Exception("Unable to get value of User Home environment variable")

        return userHome
    }
}
