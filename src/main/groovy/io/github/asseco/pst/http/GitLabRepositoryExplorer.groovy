package io.github.asseco.pst.http


import io.github.asseco.pst.infrastructure.utils.SemanticVersion
import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.Commit
import org.gitlab4j.api.models.Project
import org.gitlab4j.api.models.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Predicate
import java.util.stream.Stream

/**
 *  This class makes use of GitLab's REST API to get information about repos
 */
class GitLabRepositoryExplorer extends RepositoryExplorer {
    private static final Logger logger = LoggerFactory.getLogger(GitLabRepositoryExplorer.class)
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

        logger.info("Connecting to the Gitlab Api...")
        try {
            api = new GitLabApi(repoUrl, token)
            api.setIgnoreCertificateErrors(true)
        } catch (Exception e) {
            logger.error("An error occurred during Gitlab Api instantiation", e)
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
            logger.error("Could not find project $namespace/$projectName.", e)
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
            logger.error("Could not get SSH URL to Repo for project $namespace/$projectName.", e)
            throw e
        }

    }

    String getRepoWebUrl(String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            return project.getWebUrl()
        } catch (Exception e) {
            logger.error("Could not get Web URL to Repo for project $namespace/$projectName.", e)
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
        Project project = findProject(namespace, projectName)
        return api.getRepositoryFileApi().getFile(project, filePath, ref, true).getDecodedContentAsString()
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

        Optional<Commit> devLatestCommit = Optional.empty()
        if (api.getRepositoryApi().getBranch(project, DEVELOP_BRANCH))
            devLatestCommit = Optional.of(api.getCommitsApi().getCommit(project, "develop"))

        if (!devLatestCommit.isPresent())
            throw new RuntimeException("Unable to get '$DEVELOP_BRANCH' latest commit from Project '$namespace/$projectName'")

        return devLatestCommit.get().getId()
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

            if (!Optional.of(tag.getCommit()).isPresent())
                throw new RuntimeException("Unable to get commit from Tag '$tag.name' of Project $namespace/$projectName")

            return tag.getCommit().getId()

        } catch (Exception e) {
            logger.error("Could not get Tag $tagName hash.", e)
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
                    .filter({ tag ->
                        SemanticVersion.isValid(tag.getName())
                    })
                    .filter(predicate)
                    .collect({ tag -> SemanticVersion.create(tag.getName()).getOriginalValue() })


        } catch (Exception e) {
            logger.error("Could not get tags for project $projectName.", e)
            throw e
        }
    }
}
