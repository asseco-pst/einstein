package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.utils.SemanticVersion

class ProjectDao {
    String name
    String namespace
    String version

    ProjectDao(String aName, String aNamespace, String aVersion) {
        this.name = aName
        this.namespace = aNamespace
        this.version = validateSemanticVersion(aNamespace, aName, aVersion)
    }

    static ProjectDao fromFullName(String aFullName) {
        String namespace = aFullName.substring(0, aFullName.lastIndexOf("/"))
        String name = aFullName.substring(aFullName.lastIndexOf("/") + 1, aFullName.indexOf(":"))

        String version = aFullName.split(":").last()
        return new ProjectDao(name, namespace, version)
    }

    private String validateSemanticVersion(String aNamespace, String aName, String aVersion){
        if (aVersion.matches("\\b\\d+(\\.\\d+)*(-[a-zA-Z0-9._]+)?\\b")) {
            aVersion = SemanticVersion.findSatisfyingVersion(aNamespace, aName, aVersion)
        }
        return aVersion
    }

}
