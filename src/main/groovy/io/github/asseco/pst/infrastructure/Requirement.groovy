package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.infrastructure.utils.VersionUtils

/**
 * This class represents a single entry on the requirements.yaml file.
 * ie.:
 *
 * namespace:
 *  - projectA: range1  <---- this is an entry
 *  - projectB: range2
 */
class Requirement {

    /**
     * The project namespace/group
     */
    String namespace

    /**
     * The project name
     */
    String name

    /**
     * The requirement range according to SemVer 2.0.0
     */
    String range

    boolean isReleaseCandidate(){
        VersionUtils.isReleaseCandidate(range)
    }

    @Override
    String toString() {
        return "$namespace/$name$range"
    }
}
