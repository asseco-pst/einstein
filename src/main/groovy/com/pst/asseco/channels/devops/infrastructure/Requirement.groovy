package com.pst.asseco.channels.devops.infrastructure

class Requirement {
    String namespace
    String name
    String version

    @Override
    String toString() {
        return "$namespace/$name$version"
    }
}
