package io.github.asseco.pst.infrastructure

import groovy.json.JsonBuilder
import io.github.asseco.pst.Main
import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.crawlers.EThreadUncaughtExceptionHandler
import io.github.asseco.pst.infrastructure.crawlers.ProjectsCrawler
import io.github.asseco.pst.infrastructure.metrics.Metrics

import io.github.asseco.pst.infrastructure.utils.EinsteinProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path
import java.nio.file.Paths

@Singleton
class Einstein {
    private static final Logger logger = LoggerFactory.getLogger(Einstein.class)
    Metrics depsCalcDuration = new Metrics(Metrics.Category.DEPENDENCIES_CALCULATION_DURATION)

    static boolean isDebugModeOn() {

        if (Main.cliParser) {
            if (Main.cliParser.einsteinOptions.verbose)
                return true
        }

        return EinsteinProperties.instance().isDebugModeOn()
    }

    Path getWorkspaceFolder() {

        Path workspaceFolderPath = Paths.get([getUserHome(), EinsteinProperties.instance().getWorkspaceRootFolder()].join("/"))

        File folder = new File(workspaceFolderPath.toString())
        if (!folder.exists())
            folder.mkdirs()

        return workspaceFolderPath
    }

    Map<String, String> calcDependencies(ProjectDao aProject) {
        return calcDependencies([aProject])
    }

    Map<String, String> calcDependencies(List<ProjectDao> aProjectsData) {

        Map<String, String> parsedDeps
        DependenciesHandler depsHandler

        try {

            depsCalcDuration.startTimeTracking()

            RepoExplorerFactory.create()
            depsHandler = new DependenciesHandler(loadProjects(aProjectsData))

            ProjectsCrawler pCrawler = new ProjectsCrawler(depsHandler)
            Thread t = new Thread(pCrawler)
            EThreadUncaughtExceptionHandler handler = new EThreadUncaughtExceptionHandler(pCrawler)
            t.setUncaughtExceptionHandler(handler)
            t.start()
            t.join()

            if (handler.hasUncaughtExceptions)
                throw handler.threadTrowable

            parsedDeps = depsHandler.getParsedDependencies()

            logger.info("Detected dependencies:\n ${new JsonBuilder(parsedDeps).toPrettyString()}\n")

            depsCalcDuration.stopTimeTracking()
            logger.info("Einstein took " + depsCalcDuration.getTimeDuration())

        } catch (Exception e) {
            if(depsHandler)
                depsHandler.getThreadsManager().killLiveThreads()
            throw e
        }

        return parsedDeps
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

        if (!userHome)
            userHome = System.getenv("USERPROFILE")

        if (!userHome)
            throw new Exception("Unable to get value of User Home environment variable")

        return userHome
    }

    boolean timeout() {
        return (EinsteinProperties.instance().getMaxDuration() <= depsCalcDuration.getTimelapse())
    }
}
