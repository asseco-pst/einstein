package com.assecopst.channels.devops.infrastructure

import com.assecopst.channels.devops.http.RepoExplorerFactory
import com.assecopst.channels.devops.infrastructure.utils.Console
import com.assecopst.channels.devops.infrastructure.utils.GitUtils
import com.assecopst.channels.devops.infrastructure.utils.GitlabUtils

class Project {

    static final String REQUIREMENTS_FILE = "requirements.txt"

    String name
    String namespace
    String version
    String versionCommitSha
    String repoSshUrl
    String repoHttpsUrl
    String requirementsFileContent

    private Project() {}

    static Project factory(String aNamespace, String aName, String aVersion) {

        Project project

        try {
            project =
                    new Project.Builder()
                            .setNamespace(aNamespace)
                            .setName(aName)
                            .setVersion(aVersion)
                            .build()
        } catch (e) {
            Console.err("Unable to instantiate Project with name '${aName}' and version '${aVersion}'")
            throw e
        }

        return project
    }

    void loadRequirementsFileContent() {

        try {
            requirementsFileContent = GitlabUtils.getFileContentFromRepo(this)
        } catch (e) {
            Console.err("Unable to load ${REQUIREMENTS_FILE} file content of ${name} Project")
            throw e
        }
    }

    boolean hasRequirementsFile() {
        return requirementsFileContent
    }

    static class Builder {

        private Project project

        Builder() {
            project = new Project()
        }

        Builder setNamespace(String namespace) {
            project.namespace = namespace
            return this
        }

        Builder setName(String aName) {
            project.name = aName
            return this
        }

        Builder setVersion(String aVersion) {
            project.version = aVersion
            return this
        }

        Project build() {

            project.setRepoSshUrl(RepoExplorerFactory.get().getRepoSshUrl(project.namespace, project.name))
            project.setRepoHttpsUrl(RepoExplorerFactory.get().getRepoWebUrl(project.namespace, project.name))
            project.versionCommitSha = GitUtils.getTagCommitSha(project.repoSshUrl, project.version)

            project.loadRequirementsFileContent()

            return project
        }
    }
}
