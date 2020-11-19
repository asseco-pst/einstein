package io.github.asseco.pst.infrastructure

import com.vdurmont.semver4j.Semver
import io.github.asseco.pst.infrastructure.utils.Console
import io.github.asseco.pst.infrastructure.utils.SemanticVersion

class Housekeeper {

    Map<String, String> cleanDeps = [:]

    private Map projectsByIndex = [:]
    private Map readDependencies = [:]

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

            Console.debug("Evaluating dependencies for project ${project.ref}")

            saveProjectByIndex(project)
            addDependency(project)

            List<Project> dependencies = project.getDependencies()
            if (!dependencies)
                return

            dependencies.each {
                addDependency(it)
            }
        }
    }

    private void addDependency(Project aProject) {

        if (!readDependencies[aProject.getId()])
            readDependencies[aProject.getId()] = new HashSet<SemanticVersion>()

        if(isDependencyAlreadySaved(aProject.version.toString(), ((Set<SemanticVersion>) readDependencies[aProject.getId()])))
            return

        readDependencies[aProject.getId()] << aProject.version
    }

    private boolean isDependencyAlreadySaved(String aVersion, Set<SemanticVersion> aVersions) {

        if(!aVersions)
            return false

        return aVersions.stream().filter({ v -> v.toString() == aVersion}).collect()
    }

    private void saveProjectByIndex(Project aProject) {

        String projectRef = aProject.getRef()
        if (projectsByIndex[projectRef])
            return

        projectsByIndex[projectRef] = aProject
    }

    private void collectFinalDependencies() {

        projectsByIndex.each {
            Project project = (Project) it.value

            if (!isAcceptedDependency(project))
                return

            List<Project> dependencies = []
            dependencies << project

            if (project.getDependencies())
                dependencies.addAll(project.getDependencies())

            filterAcceptedDependencies(dependencies).each { acceptedDependency ->
                cleanDeps.put(acceptedDependency.id, acceptedDependency.version.getOriginalValue())
            }
        }
    }

    private void checkVersionsCompatibility() {

        readDependencies.each {
            String projectName = it.key
            Set<SemanticVersion> dependentVersions = (Set<SemanticVersion>) it.value

            if (dependentVersions.size() <= 1)
                return

            Console.warn("Checking if the multiple versions found for Project ${projectName} are semantically compatible...")

            if (SemanticVersion.hasNonCompatibleVersions(dependentVersions)) {
                Console.warn("Found non compatible versions for Project '${projectName}': ${dependentVersions.join(" <> ")}")
                throw new Exception("Non compatible versions found!")
            }
        }
    }

    private void resolveAmbiguousDependencies() {

        readDependencies.each { projectRef, d ->
            Set<SemanticVersion> dependencies = (Set<SemanticVersion>) d
            if (dependencies.size() == 1)
                return

            keepBiggestVersion(dependencies)
        }
    }

    private void keepBiggestVersion(Set<SemanticVersion> aVersions) {


        Semver biggestVersion =
                SemanticVersion.getBiggestSanitizedVersion(
                        aVersions.stream()
                                .collect({
                                    it.getOriginalValue()
                                })
                )

        Iterator<SemanticVersion> versionsIterator = aVersions.iterator()
        while (versionsIterator.hasNext()) {
            SemanticVersion currVersion = versionsIterator.next()
            if (currVersion.toString() != biggestVersion.getOriginalValue())
                versionsIterator.remove()
        }
    }

    private List<Project> filterAcceptedDependencies(List<Project> aDependencies) {

        return aDependencies.stream().filter({ d -> isAcceptedDependency(d) }).collect()
    }

    private boolean isAcceptedDependency(Project aProject) {

        if (!readDependencies[aProject.getId()])
            return false
        if (!((Set) readDependencies[aProject.getId()]).contains(aProject.version))
            return false
        return true
    }

    private void printRawDependencies() {

        Console.info("Raw dependencies list:\n")
        projectsByIndex.each { p ->
            String projectRef = p.key
            Project project = (Project) p.value

            if (!project.getDependencies())
                return

            Console.print("$projectRef:")
            project.getDependencies().each { d ->
                Console.print("   >> ${d.getRef()}")
            }
        }
        Console.print("\n")
        Console.info("Calculated dependencies per Project:")
        Console.print("$readDependencies\n")
    }
}
