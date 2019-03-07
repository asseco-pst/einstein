package com.assecopst.channels.devops.http

import java.util.regex.Pattern

abstract class RepositoryExplorer {

    String repoUrl
    String token

    protected RepositoryExplorer(String aRepoUrl, String aToken) {
        repoUrl = aRepoUrl
        token = aToken

        connect()
    }

    abstract void connect()

    abstract String getRepoSshUrl(String namespace, String projectName)

    abstract String getRepoWebUrl(String namespace, String projectName)

    abstract String getHttpUrlToRepo(String namespace, String projectName)

    abstract String getFileContents(String filePath, String ref, String namespace, String projectName)

    abstract String getTagHash(String tagName, String namespace, String projectName)

    abstract List listTags(String namespace, String projectName, Pattern regex)

}