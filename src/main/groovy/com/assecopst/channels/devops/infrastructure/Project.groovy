package com.asseco.pst.devops.infrastructure

import com.asseco.pst.devops.infrastructure.utils.Console
import com.asseco.pst.devops.infrastructure.utils.GitUtils
import com.asseco.pst.devops.infrastructure.utils.GitlabUtils

class Project {

    static final String requirementsFilename = "requirements.txt"

    String name
    String version
    String versionCommitSha
    String repoSshUrl
    String repoHttpsUrl
    String requirementsFileContent

    Project(String aName, String aVersion, String aRepoHttpsUrl, String aRepoSshUrl) {
        name = aName
        version = aVersion
        repoHttpsUrl = aRepoHttpsUrl
        repoSshUrl = aRepoSshUrl

        versionCommitSha = GitUtils.getTagCommitSha(repoSshUrl, version)
    }

    void loadRequirementsFileContent() {

        try {
            requirementsFileContent = GitlabUtils.getFileContentFromRepo(this)
        } catch (e) {
            Console.err("Unable to load ${requirementsFilename} file content of ${name} Project")
            throw  e
        }
    }

    boolean hasRequirementsFile() {
        return requirementsFileContent
    }
}
