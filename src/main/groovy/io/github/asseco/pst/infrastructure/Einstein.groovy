package io.github.asseco.pst.infrastructure


import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.cli.CliParser
import io.github.asseco.pst.infrastructure.crawlers.ProjectsCrawler
import io.github.asseco.pst.infrastructure.metrics.Metrics
import io.github.asseco.pst.infrastructure.utils.Console
import io.github.asseco.pst.infrastructure.utils.EinsteinProperties

import java.nio.file.Path
import java.nio.file.Paths

//@Singleton
class Einstein {

    CliParser cli

    DependenciesManager dpManager
    ProjectsManager projectsManager
    synchronized List<Project> scannedDependencies
    Metrics metrics
    EinsteinProperties properties

    Einstein() {
        scannedDependencies = []
        metrics =  new Metrics()
        dpManager = new DependenciesManager()
        properties =  new EinsteinProperties()
        projectsManager = new ProjectsManager()
    }

    CliParser getCli() {
        if (!cli)
            cli = new CliParser()
        return cli
    }

    boolean isDebugModeOn() {
        if (cli) {
            if (cli.einsteinOptions.verbose)
                return true
        }

        return properties.isDebugModeOn()
    }

    void addScannedProject(Project aProject) {
        if (!scannedDependencies.contains(aProject))
            scannedDependencies << aProject
    }

    Path getWorkspaceFolder() {

        Path workspaceFolderPath = Paths.get([getUserHome(), properties.getWorkspaceRootFolder()].join("/"))

        File folder = new File(workspaceFolderPath.toString())
        if(!folder.exists())
            folder.mkdirs()

        return workspaceFolderPath
    }

    void calcDependencies(ProjectDao aProject) {
        calcDependencies([aProject])
    }

    void calcDependencies(List<ProjectDao> aProjectsData) {
        
        metrics.startTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)
        RepoExplorerFactory.create()

        ProjectsCrawler pCrawler = new ProjectsCrawler(loadProjects(aProjectsData))
        pCrawler.start()
        pCrawler.join()

        metrics.stopTimeTracking(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION)

        dpManager.resolveVersions(scannedDependencies)

        Console.print("\n\n")
        Console.info("Detected dependencies:")
        Console.printMap(dpManager.getCalcDependencies())
        Console.print("\n\n")

        Console.info("Einstein took " +
                metrics.getTimeDuration(Metrics.METRIC.DEPENDENCIES_CALCULATION_DURATION).toString())
    }

    Map<String, String> getCalculatedDependencies() {
        return dpManager.getCalcDependencies()
    }

    private List<Project> loadProjects(List<ProjectDao> aProjectsData) {

        List<Project> projects = []
        aProjectsData.each {
            projects << Project.factory(it.namespace, it.name, it.version)
        }

        return projects
    }

    private String getUserHome() {

        String userHome = System.getenv("HOME")

        if(!userHome)
            userHome = System.getenv("USERPROFILE")

        if(!userHome)
            throw new Exception("Unable to get value of User Home environment variable")

        return userHome
    }
}
