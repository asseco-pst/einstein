package com.assecopst.channels.devops.http

import com.assecopst.channels.devops.infrastructure.utils.Console
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.Tag

import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *  This class makes use of GitLab's REST API to get information about repos
 */
class GitLabRepositoryExplorer extends RepositoryExplorer {

    GitLabApi api

    GitLabRepositoryExplorer(String aRepoUrl, String aToken) {
        super(aRepoUrl, aToken)
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

    /**
     *  Returns a project's Http URL given its namespace (ie. group) and its project name
     *
     * @param the namespace of the project (ie. group)
     * @param the name of the project
     * @return the HTTP URL to the project repository
     */
    @Override
    String getHttpUrlToRepo(String namespace, String projectName) {

        try {
            Project project = findProject(namespace, projectName)
            return project.getHttpUrlToRepo()
        } catch (Exception e) {
            Console.err("Could not get Http URL to Repo for project $namespace/$projectName. Cause: $e")
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
            return api.getTagsApi().getTag(project, tagName).getCommit().getId()
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
    List<Tag> listTags(String namespace, String projectName, Predicate<? super Tag> predicate = null) {
        try {
            Project project = findProject(namespace, projectName)

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)

            if(predicate != null)
                return tags.filter(predicate).collect(Collectors.toList())
            else
                return tags.collect(Collectors.toList())

        } catch (Exception e) {
            Console.err("Could not get tags for project $projectName. Cause: $e")
            throw e
        }
    }
}
