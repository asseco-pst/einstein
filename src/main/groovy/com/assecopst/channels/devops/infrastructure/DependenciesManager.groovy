package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.version.Version

class DependenciesManager {

    Map dependencies = [:]


    void addDependency(String aProjectName, String aVersion) {
        if (!dependencies[aProjectName])
            dependencies[aProjectName] = new HashSet<String>()
        dependencies[aProjectName] << aVersion
    }

    void checkVersionsCompatibility() {

        boolean foundNonCompatibleVersions = false

        dependencies.each {
            String projectName = it.key
            Set<String> dependentVersions = (Set<String>) it.value

            if (dependentVersions.size() <= 1)
                return

            Console.info("Identified multiple versions for Project ${projectName}. Are they compatible? Let's check...")

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
}
