package io.github.asseco.pst.infrastructure.utils

import io.github.asseco.pst.infrastructure.interfaces.Version

class NonSemanticVersion implements Version {
    private String version

    public NonSemanticVersion(String version) {
        this.version = version
    }

    @Override
    String getVersion() {
        return version
    }

    @Override
    boolean isSnapshot() {
        return false
    }
}
