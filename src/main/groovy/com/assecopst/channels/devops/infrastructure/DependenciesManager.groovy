package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.version.Version

class DependenciesManager {

    Map finalDependencies = [:]

    private Map projectsByIndex = [:]
    private Map readDependencies = [:]


    void resolveVersions(List<Project> aScannedProjects) {

        collectDependencies(aScannedProjects)

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

        String projectRef = aProject.getProjectRef()
        if (!readDependencies[projectRef])
            readDependencies[projectRef] = new HashSet<String>()
        readDependencies[projectRef] << aProject.version
    }

    private void saveProjectByIndex(Project aProject) {

        String projectRef = aProject.getProjectRef()
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
                finalDependencies.put(acceptedDependency.name, acceptedDependency.version)
            }
        }
    }

    private void checkVersionsCompatibility() {

        readDependencies.each {
            String projectName = it.key
            Set<String> dependentVersions = (Set<String>) it.value

            if (dependentVersions.size() <= 1)
                return

            Console.warn("Identified multiple versions for Project ${projectName}. Are they compatible? Let's check...")

            if (hasNonCompatibleVersions(dependentVersions)) {
                Console.warn("Found non compatible versions for Project '${projectName}': ${dependentVersions.join(" <> ")}")
                throw new Exception("Non compatible versions found!")
            }
        }
    }


    private boolean hasNonCompatibleVersions(Set<String> aVersions) {

        List<Version> versions = Version.factory(aVersions)
        return (Version.hasMultipleVersionSpecifications(versions) || Version.hasNonBackwardCompatibleVersions(versions))
    }

    private void resolveMultiVersionsDependencies() {

        readDependencies.each {
            String projectRef = it.key
            Set<String> pReadDependencies = (Set<String>) it.value

            if (pReadDependencies.size() == 1)
                return

            String biggestVersion = Version.getBiggestVersion(pReadDependencies)

            pReadDependencies.each { version ->
                if (version != biggestVersion)
                    ((Set) readDependencies[projectRef]).remove(version)
            }
        }
    }

    private List<Project> filterAcceptedDependencies(List<Project> aDependencies) {

        return aDependencies.stream().filter({ d -> isAcceptedDependency(d) }).collect()
    }

    private boolean isAcceptedDependency(Project aProject) {

        if (!readDependencies[aProject.getProjectRef()])
            return false
        if (!((Set) readDependencies[aProject.getProjectRef()]).contains(aProject.version))
            return false
        return true
    }
}
