package io.github.asseco.pst.http


import io.github.asseco.pst.infrastructure.utils.Console
import io.github.asseco.pst.infrastructure.version.Version
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.Tag

import java.util.function.Predicate
import java.util.stream.Stream

/**
 *  This class makes use of GitLab's REST API to get information about repos
 */
class GitLabRepositoryExplorer extends RepositoryExplorer {

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

        try {
            api = new GitLabApi(repoUrl, token)
            api.setIgnoreCertificateErrors(true)
        } catch (Exception e) {
            Console.err("An error occurred during Gitlab Api instantiation. Cause: ${e}")
            throw e
        }
    }

    /**
     * Returns a Project identified by a namespace and a project name
     *
     * @param the namespace of the project (ie. its group)
     * @param the name of the project
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
     *  Returns a project's SSH URL given its namespace (ie. group) and its project name
     *
     * @param the namespace of the project (ie. group)
     * @param the name of the project
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
     * @param ref name of a branch, commit, tag, etc.
     * @param namespace the namespace of the project (ie. group)
     * @param projectName the name of the project
     * @return the contents of the file
     */
    @Override
    String getFileContents(String filePath, String ref, String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            return api.getRepositoryFileApi().getFile(project, filePath, ref, true).getDecodedContentAsString()
        } catch (Exception e) {
            Console.err("Could not get file $filePath from project $namespace/$projectName. Cause: $e")
            throw e
        }
    }

    /**
     *  Gets a tag's hash (ie. commit ID) given a project
     *
     * @param tagName the tag name (eg. v1.4.6)
     * @param namespace the namespace of the project (ie. group)
     * @param projectName the name of the project
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
     * @param namespace the namespace of the project (ie. group)
     * @param projectName the name of the project
     * @param regex a pattern to filter the tags
     * @return a list of tags
     */
    @Override
    List<String> listTags(String namespace, String projectName, Predicate<? super Tag> predicate = null) {
        try {
            Project project = findProject(namespace, projectName)

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)

            if (predicate != null) {
                return tags
                        .filter(predicate)
                        .collect({ tag -> Version.extractVersionFrom(tag.getName()) })
            } else {
                return tags
                        .collect({ tag -> Version.extractVersionFrom(tag.getName()) })
            }

        } catch (Exception e) {
            Console.err("Could not get tags for project $projectName. Cause: $e")
            throw e
        }
    }
}
