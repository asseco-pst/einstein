package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.version.Version

class DependenciesManager {

    Map readDependencies = [:]
    Map finalDependencies = [:]

    void addDependency(String aProjectName, String aVersion) {
        if (!readDependencies[aProjectName])
            readDependencies[aProjectName] = new HashSet<String>()
        readDependencies[aProjectName] << aVersion
    }

    void resolveVersions() {

        checkVersionsCompatibility()
        resolveMultiVersionsDependencies()
    }

    private void checkVersionsCompatibility() {

        boolean foundNonCompatibleVersions = false

        readDependencies.each {
            String projectName = it.key
            Set<String> dependentVersions = (Set<String>) it.value

            if (dependentVersions.size() <= 1)
                return

            Console.warn("Identified multiple versions for Project ${projectName}. Are they compatible? Let's check...")

            if (hasNonCompatibleVersions(dependentVersions)) {
                Console.warn("Found non compatible versions for Project '${projectName}': ${dependentVersions.join(" <> ")}")
                foundNonCompatibleVersions = true
            }
        }
        if (foundNonCompatibleVersions)
            throw new Exception("Non compatible versions found!")
    }


    private boolean hasNonCompatibleVersions(Set<String> aVersions) {

        List<Version> versions = Version.factory(aVersions)
        return (Version.hasMultipleVersionSpecifications(versions) || Version.hasNonBackwardCompatibleVersions(versions))
    }

    private void resolveMultiVersionsDependencies() {

        readDependencies.each {
            String projectName = it.key
            Set<String> readDependencies = (Set<String>) it.value

            if (readDependencies.size() == 1) {
                finalDependencies[projectName] = readDependencies[0]
                return
            }

            finalDependencies[projectName] = Version.getBiggestVersion(readDependencies)
        }
    }
}
