package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.interfaces.Version
import io.github.asseco.pst.infrastructure.utils.NonSemanticVersion
import io.github.asseco.pst.infrastructure.utils.SemanticVersion

class VersionFactory {
     static Version createVersion(String version) {
        // Simple heuristic: use SemanticVersion if input contains dots, otherwise NonSemanticVersion
        if (version.matches("\\b\\d+(\\.\\d+)*(-[a-zA-Z0-9._]+)?\\b")) {
            return SemanticVersion.create(version)
        } else {
            return new NonSemanticVersion(version)
        }
    }
}