package com.pst.asseco.channels.devops.http

import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.utils.SemanticVersion
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.Tag

import java.util.function.Predicate
import java.util.stream.Stream

/**
 *  This class makes use of GitLab's REST API to get information about repos
 */
class GitLabRepositoryExplorer extends RepositoryExplorer {

    private final String DEVELOP_BRANCH = "develop"

    GitLabApi api


    @Override
    protected void setRepoURLEnvVar() {
        repoURLEnvVar = "GITLAB_URL"
    }

    @Override
    protected void setTokenEnvVar() {
        tokenEnvVar = "GITLAB_TOKEN"
    }

    @Override
    void connect() {

        Console.info("Connecting to the Gitlab Api...")
        try {
            api = new GitLabApi(repoUrl, token)
            api.setIgnoreCertificateErrors(true)
        } catch (Exception e) {
            Console.err("An error occurred during Gitlab Api instantiation. Cause: ${e}")
            throw e
        }
    }

    /**
     * Returns a Project identified by a projectNamespace and a project projectName
     *
     * @param the projectNamespace of the project (ie. its group)
     * @param the projectName of the project
     * @return the Project
     */
    Project findProject(String namespace, String projectName) {

        try {
            Project project = api.getProjectApi().getProject(namespace, projectName)

            return project
        } catch (Exception e) {
            Console.err("Could not find project $namespace/$projectName. Cause: $e")
            throw e
        }

    }

    /**
     *  Returns a project's SSH URL given its projectNamespace (ie. group) and its project projectName
     *
     * @param the projectNamespace of the project (ie. group)
     * @param the projectName of the project
     * @return the SSH URL to the project repository
     */
    @Override
    String getRepoSshUrl(String namespace, String projectName) {

        try {
            Project project = findProject(namespace, projectName)
            return project.getSshUrlToRepo()
        } catch (Exception e) {
            Console.err("Could not get SSH URL to Repo for project $namespace/$projectName. Cause: $e")
            throw e
        }

    }

    String getRepoWebUrl(String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            return project.getWebUrl()
        } catch (Exception e) {
            Console.err("Could not get Web URL to Repo for project $namespace/$projectName. Cause: $e")
            throw e
        }
    }

    /**
     *  Get the contents of a file in filePath, with version ref.
     *
     * @param filePath
     * @param ref projectName of a branch, commit, tag, etc.
     * @param namespace the projectNamespace of the project (ie. group)
     * @param projectName the projectName of the project
     * @return the contents of the file
     */
    @Override
    String getFileContents(String filePath, String ref, String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            return api.getRepositoryFileApi().getFile(project, filePath, ref, true).getDecodedContentAsString()
        } catch (Exception e) {
            throw e
        }
    }

    /**
     * Get the hash of the latest commit on the 'develop' branch
     *
     * @param namespace the projectNamespace of the projecy
     * @param projectName the projectName of the project
     * @return the SHA-1 hash of the identified commit
     * @throws Exception if the Project does not contains a 'develop' named branch
     */
    @Override
    String getDevelopBranchLatestCommitSha(String namespace, String projectName) {

        Project project = findProject(namespace, projectName)

        if(api.getRepositoryApi().getBranch(project, DEVELOP_BRANCH))
            return api.getCommitsApi().getCommit(project, "develop").getId()

        throw new RuntimeException("Project '$namespace/$projectName' does not contains a '$DEVELOP_BRANCH' named branch")
    }

    /**
     *  Gets a tag's hash (ie. commit ID) given a project
     *
     * @param tagName the tag projectName (eg. v1.4.6)
     * @param namespace the projectNamespace of the project (ie. group)
     * @param projectName the projectName of the project
     * @return the SHA-1 hash of the tag
     */
    @Override
    String getTagHash(String tagName, String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)

            Tag tag = tags.filter({ tag -> tag.getName().endsWith(tagName) }).findFirst().get()

            return tag.getCommit().getId()

        } catch (Exception e) {
            Console.err("Could not get Tag $tagName hash. Cause: $e")
            throw e
        }
    }

    /**
     *  Returns a list of tags existing in the projects repo.
     *  If provided a regular expression, this method will return only tags that match with the pattern.
     *
     * @param namespace the projectNamespace of the project (ie. group)
     * @param projectName the projectName of the project
     * @param regex a pattern to filter the tags
     * @return a list of tags
     */
    @Override
    List<String> listTags(String namespace, String projectName, Predicate<? super Tag> predicate) {
        try {
            Project project = findProject(namespace, projectName)

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)

            return tags
                    .filter(predicate)
                    .collect({ tag -> SemanticVersion.create(tag.getName()).toString() })

        } catch (Exception e) {
            Console.err("Could not get tags for project $projectName. Cause: $e")
            throw e
        }
    }
}
