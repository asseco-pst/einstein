package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import io.github.asseco.pst.infrastructure.utils.SemanticVersion
import org.slf4j.Logger

class Housekeeper {
    private static final Logger logger = LoggerFactory.getLogger(Housekeeper.class)
    Map<String, String> cleanDeps = [:]

    private Map projectsByIndex = [:]
    private Map<String, Map<SemanticVersion, String>> readDependencies = [:]

    /**
     * Resolve calculated dependencies according with the following policies:
     *  - when detecting multiple versions of a same dependency/project:
     *      - check if those dependencies' versions are semantically compatible
     *      - if they're compatible, select the highest one
     *
     * @param aScannedProjects
     */
    void resolve(List<Project> aScannedProjects) {
        collectDependencies(aScannedProjects)
        printRawDependencies()

        checkVersionsCompatibility()
        resolveAmbiguousDependencies()

        collectFinalDependencies()
    }

    private void collectDependencies(List<Project> aProjects) {
        aProjects.each { project ->
            logger.debug("Evaluating dependencies for project ${project.ref}")

            saveProjectByIndex(project)
            addDependency(project)

            List<Project> dependencies = project.getDependencies()
            if (!dependencies) {
                return
            }

            dependencies.each {
                addDependency(it)
            }
        }
    }

    private void addDependency(Project aProject) {
        if (!readDependencies[aProject.getId()]) {
            readDependencies[aProject.getId()] = new HashMap<SemanticVersion, String>()
        }

        if (isDependencyAlreadySaved(aProject.version.toString(), readDependencies[aProject.getId()])) {
            return
        }

        readDependencies[aProject.getId()].put(aProject.version, aProject.parentProjectRef ?: "")
    }

    private static boolean isDependencyAlreadySaved(String aVersion, Map<SemanticVersion, String> aVersions) {
        if (!aVersions) {
            return false
        }
        return aVersions.entrySet().stream().filter { it.getKey().toString() == aVersion }.collect()
    }

    private void saveProjectByIndex(Project aProject) {
        String projectRef = aProject.getRef()
        if (projectsByIndex[projectRef]) {
            return
        }

        projectsByIndex[projectRef] = aProject
    }

    private void collectFinalDependencies() {

        projectsByIndex.each {
            Project project = (Project) it.value

            if (!isAcceptedDependency(project)) {
                return
            }

            List<Project> dependencies = []
            dependencies << project

            if (project.getDependencies()) {
                dependencies.addAll(project.getDependencies())
            }

            filterAcceptedDependencies(dependencies).each { acceptedDependency ->
                cleanDeps.put(acceptedDependency.id, acceptedDependency.version.getOriginalValue())
            }
        }
    }

    private void checkVersionsCompatibility() {
        readDependencies.each {
            String projectName = it.key
            Map<SemanticVersion, String> dependentVersions = it.value

            if (dependentVersions.size() <= 1) {
                return
            }

            logger.warn("Checking if the multiple versions found for project ${projectName} are semantically compatible...")

            if (SemanticVersion.hasNonCompatibleVersions(dependentVersions)) {
                logger.warn("Found non compatible versions for project '${projectName}':")
                Set<SemanticVersion> incompatibleDeps = []

                dependentVersions.each {
                    logger.warn("   >> ${it.value ? "Project '${it.value}'" : "Current project"} declares version ${it.key}")
                    incompatibleDeps << it.key
                }

                logger.error("Declared versions are non compatible: ${incompatibleDeps.join(" <> ")}")
                throw new Exception("Non compatible versions found!")
            }
            logger.info("The multiple versions found for project ${projectName} are semantically compatible...")
        }
    }

    private void resolveAmbiguousDependencies() {
        readDependencies.each { projectRef, dependencies ->
            if (dependencies.size() == 1) {
                return
            }

            keepBiggestVersion(dependencies)
        }
    }

    private void keepBiggestVersion(Map<SemanticVersion, String> aVersions) {
        String biggestVersion = SemanticVersion.getBiggestVersion(aVersions)
        Iterator<Map.Entry<SemanticVersion, String>> versionsIterator = aVersions.iterator()

        while (versionsIterator.hasNext()) {
            SemanticVersion currVersion = versionsIterator.next().key

            if (currVersion.toString() != biggestVersion) {
                versionsIterator.remove()
            }
        }
    }

    private List<Project> filterAcceptedDependencies(List<Project> aDependencies) {
        return aDependencies.stream().filter { d -> isAcceptedDependency(d) }.collect()
    }

    private boolean isAcceptedDependency(Project aProject) {
        if (!readDependencies[aProject.getId()]) {
            return false
        }

        if (!(readDependencies[aProject.getId()].containsKey(aProject.version))) {
            return false
        }

        return true
    }

    private void printRawDependencies() {
        logger.info("Raw dependencies list:\n")
        projectsByIndex.each { p ->
            String projectRef = p.key
            Project project = (Project) p.value

            if (!project.getDependencies()) {
                return
            }

            logger.info("$projectRef:")
            project.getDependencies().each { d ->
                logger.info("   >> ${d.getRef()}")
            }
        }
    }
}
