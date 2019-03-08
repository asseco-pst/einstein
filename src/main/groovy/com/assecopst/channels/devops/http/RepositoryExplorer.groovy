package com.assecopst.channels.devops.http

import org.gitlab4j.api.models.Tag

import java.util.function.Predicate

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

    abstract List<String> listTags(String namespace, String projectName, Predicate<? super Tag> predicate)

}