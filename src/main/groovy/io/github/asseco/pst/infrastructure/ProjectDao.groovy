package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.utils.SemanticVersion

class ProjectDao {
    String name
    String namespace
    String version

    ProjectDao(String aName, String aNamespace, String aVersion) {
        this.name = aName
        this.namespace = aNamespace
        this.version = SemanticVersion.findSatisfyingVersion(aNamespace, aName, aVersion)
    }

    static ProjectDao fromFullName(String aFullName) {
        String namespace = aFullName.split("/").first()
        String name = aFullName.split("/")[1].split(":").first()

        String version = aFullName.split(":").last()
        return new ProjectDao(name, namespace, version)
    }
}
