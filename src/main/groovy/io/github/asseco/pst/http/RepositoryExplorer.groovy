package io.github.asseco.pst.http

import org.gitlab4j.api.models.Tag

import java.util.function.Predicate

abstract class RepositoryExplorer {
    protected String repoURLEnvVar
    protected String tokenEnvVar

    protected String repoUrl
    protected String token

    protected RepositoryExplorer() {
        setRepoURLEnvVar()
        setTokenEnvVar()

        loadRepoUrl()
        loadAccessToken()
        connect()
    }

    protected abstract void setRepoURLEnvVar()

    protected abstract void setTokenEnvVar()

    protected void loadRepoUrl() {
        repoUrl = System.getenv(repoURLEnvVar)
        if (!repoUrl) {
            throw new IllegalArgumentException("Environment variable '$repoURLEnvVar' is undefined.")
        }

    }

    protected void loadAccessToken() {
        token = System.getenv(tokenEnvVar)
        if (!token) {
            throw new IllegalArgumentException("Environment variable '$tokenEnvVar' is undefined.")
        }
    }

    abstract void connect()

    abstract String getRepoSshUrl(String namespace, String projectName)

    abstract String getRepoWebUrl(String namespace, String projectName)

    abstract String getFileContents(String filePath, String ref, String namespace, String projectName)

    abstract String getTagHash(String tagName, String namespace, String projectName)

    abstract List<String> listTags(String namespace, String projectName, Predicate<? super Tag> predicate)

    abstract String getDevelopBranchLatestCommitSha(String namespace, String projectName)

    abstract String getSpecificBranchLatestCommitSha(String namespace, String projectName, String branchName)

}