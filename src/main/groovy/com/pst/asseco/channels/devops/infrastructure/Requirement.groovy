package com.pst.asseco.channels.devops.infrastructure

import com.pst.asseco.channels.devops.infrastructure.utils.VersionUtils

class Requirement {
    String namespace
    String name
    String range

    boolean isReleaseCandidate(){
        VersionUtils.isReleaseCandidate(range)
    }

    @Override
    String toString() {
        return "$namespace/$name$range"
    }
}
