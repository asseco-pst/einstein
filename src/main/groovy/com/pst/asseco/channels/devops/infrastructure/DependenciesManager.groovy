package com.pst.asseco.channels.devops.infrastructure

import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.version.Version

class DependenciesManager {

    Map finalDependencies = [:]

    private Map projectsByIndex = [:]
    private Map readDependencies = [:]


    void resolveVersions(List<Project> aScannedProjects) {

        collectDependencies(aScannedProjects)
        printRawDependencies()

        checkVersionsCompatibility()
        resolveMultiVersionsDependencies()

        collectFinalDependencies()
    }

    private void collectDependencies(List<Project> aProjects) {

        aProjects.each { project ->
            saveProjectByIndex(project)
            addDependency(project)

            List<Project> dependencies = project.getDependencies()
            if (!dependencies)
                return

            dependencies.each {
                Project dependency = (Project) it
                addDependency(dependency)
            }
        }
    }

    private void addDependency(Project aProject) {

        if (!readDependencies[aProject.getId()])
            readDependencies[aProject.getId()] = new HashSet<Version>()

        if(isDependencyAlreadySaved(aProject.version.versionStr, ((Set<Version>) readDependencies[aProject.getId()])))
            return

        readDependencies[aProject.getId()] << aProject.version
    }

    private boolean isDependencyAlreadySaved(String aVersion, Set<Version> aVersions) {

        if(!aVersions)
            return false

        return aVersions.stream().filter({ v -> v.versionStr == aVersion}).collect()
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
                finalDependencies.put(acceptedDependency.id, acceptedDependency.version.toString())
            }
        }
    }

    private void checkVersionsCompatibility() {

        readDependencies.each {
            String projectName = it.key
            Set<Version> dependentVersions = (Set<Version>) it.value

            if (dependentVersions.size() <= 1)
                return

            Console.warn("Identified multiple versions for Project ${projectName}. Are they compatible? Let's check...")

            if (hasNonCompatibleVersions(dependentVersions)) {
                Console.warn("Found non compatible versions for Project '${projectName}': ${dependentVersions.join(" <> ")}")
                throw new Exception("Non compatible versions found!")
            }
        }
    }

    private boolean hasNonCompatibleVersions(Set<Version> aVersions) {

        return (Version.hasMultipleVersionSpecifications(aVersions) || Version.hasNonBackwardCompatibleVersions(aVersions))
    }


    private void resolveMultiVersionsDependencies() {

        readDependencies.each { projectRef, d ->

            Set<Version> dependencies = (Set<Version>) d
            if (dependencies.size() == 1)
                return

            String biggestVersion = Version.getBiggestVersion(dependencies)

            Iterator<Version> versionsIterator = dependencies.iterator()
            while (versionsIterator.hasNext()) {
                String version = versionsIterator.next()
                if (version != biggestVersion)
                    versionsIterator.remove()
            }
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

        Console.info("Check raw dependencies:\n")
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
        Console.info("Check required dependencies by Project:")
        Console.print("$readDependencies\n")
    }
}
