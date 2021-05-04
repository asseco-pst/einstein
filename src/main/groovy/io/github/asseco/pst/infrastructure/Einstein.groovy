package io.github.asseco.pst.infrastructure

import groovy.json.JsonBuilder
import io.github.asseco.pst.infrastructure.crawlers.ProjectsCrawler
import io.github.asseco.pst.infrastructure.exceptions.UncaughtExceptionsManager
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import io.github.asseco.pst.infrastructure.metrics.Metrics
import io.github.asseco.pst.infrastructure.threads.ThreadPoolManager
import io.github.asseco.pst.infrastructure.utils.EinsteinProperties
import org.slf4j.Logger

import java.nio.file.Path
import java.nio.file.Paths

@Singleton
class Einstein {
    private static final Logger logger = LoggerFactory.getLogger(Einstein.class)
    Metrics depsCalcDuration = new Metrics(Metrics.Category.DEPENDENCIES_CALCULATION_DURATION)

    Path getWorkspaceFolder() {
        Path workspaceFolderPath = Paths.get([getUserHome(), EinsteinProperties.instance().getWorkspaceRootFolder()].join("/"))
        File folder = new File(workspaceFolderPath.toString())

        if (!folder.exists()) {
            folder.mkdirs()
        }

        return workspaceFolderPath
    }

    Map<String, String> calcDependencies(ProjectDao aProject) {
        return calcDependencies([aProject])
    }

    Map<String, String> calcDependencies(List<ProjectDao> aProjectsData) {
        Map<String, String> parsedDeps
        DependenciesHandler depsHandler

        ThreadPoolManager.instance.initializePool()
        UncaughtExceptionsManager.instance.reset()

        depsCalcDuration.startTimeTracking()
        depsHandler = new DependenciesHandler(loadProjects(aProjectsData))

        ProjectsCrawler pCrawler = new ProjectsCrawler(depsHandler)
        ThreadPoolManager.instance.submitWorker(pCrawler).get()

        UncaughtExceptionsManager.instance.checkUncaughtExceptions()
        parsedDeps = depsHandler.getParsedDependencies()

        logger.info("Detected dependencies:\n ${new JsonBuilder(parsedDeps).toPrettyString()}\n")
        depsCalcDuration.stopTimeTracking()

        logger.info("It took ${depsCalcDuration.getTimeDuration()} to calculate the dependencies.")
        return parsedDeps
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

        if (!userHome) {
            userHome = System.getenv("USERPROFILE")
        }

        if (!userHome) {
            throw new Exception("Unable to get value of User Home environment variable")
        }

        return userHome
    }

    boolean timeout() {
        return (EinsteinProperties.instance().getMaxDuration() <= depsCalcDuration.getTimelapse())
    }

    void shutdown() {
        ThreadPoolManager.instance.shutdownPool()
    }
}
