package io.github.asseco.pst.infrastructure.interfaces

interface Version {
    String getVersion() // Returns the version as a string
    boolean isSnapshot()
}
