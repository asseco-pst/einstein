package com.pst.asseco.channels.devops.infrastructure

class ProjectDao {

    String name
    String namespace
    String version

    static ProjectDao fromFullName(String fullName){
        String namespace = fullName.split("/").first()
        String name = fullName.split("/")[1].split(":").first()
        String version = fullName.split(":").last()
        return new ProjectDao(namespace: namespace, name: name, version: version)
    }

}
