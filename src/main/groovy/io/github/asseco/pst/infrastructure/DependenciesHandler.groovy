package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.crawlers.MinionsFactory
import io.github.asseco.pst.infrastructure.crawlers.Worker
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import io.github.asseco.pst.infrastructure.utils.ThreadsManager
import org.slf4j.Logger

class DependenciesHandler {

    private static final Logger logger = LoggerFactory.getLogger(DependenciesHandler.class)
    List<Project> projects
    List<Project> scannedDeps
    ThreadsManager threadsManager

    private Housekeeper housekeeper
    private Set<String> crawledProjects

    DependenciesHandler(List<Project> aProjects) {
        projects = aProjects
        scannedDeps = []

        crawledProjects = []
        housekeeper = new Housekeeper()
        threadsManager = new ThreadsManager()
    }

    void calcDependencies(Project aProject, Worker aObserver) {
        if (isAlreadyCrawled(aProject)) {
            return
        }

        addCrawledProject(aProject)
        logger.debug("Launching FileParserMinion to calculate dependencies of project ${aProject.ref}")
        MinionsFactory.launch(MinionsFactory.Type.CRAWLER, aProject, aObserver, this)
    }

    private boolean isAlreadyCrawled(Project aProject) {
        String ref = aProject.ref
        if (crawledProjects.contains(ref)) {
            logger.debug("Project '${ref}' was already crawled...")
            return true
        }
        crawledProjects << ref
        return false
    }

    /**
     * Add a new crawled Project
     * A crawled Project is a project that has been identified as a dependency of some other project but it is not yet scanned,
     * i.e, einstein did not yet identified the project dependencies itself.
     * @param aProject
     */
    private void addCrawledProject(Project aProject) {
        crawledProjects << aProject.ref
    }

    /**
     * Add a new scanned Project
     * A scanned Project means that einstein already have identified Project's dependencies
     * @param aProject
     */
    void addScannedProject(Project aProject) {
        if (!scannedDeps.contains(aProject)) {
            scannedDeps << aProject
        }
    }

    Map<String, String> getParsedDependencies() {
        housekeeper.resolve(scannedDeps)
        return housekeeper.getCleanDeps()
    }
}
