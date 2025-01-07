package io.github.asseco.pst.http

import groovy.json.JsonSlurper
import io.github.asseco.pst.infrastructure.utils.SemanticVersion
import org.gitlab4j.api.GitLabApi
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
    final static String DEVELOP_BRANCH = "develop"
    final static String NEXT_BRANCH = "next"

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
        logger.info("Connecting to the Gitlab API...")

        try {
            api = new GitLabApi(repoUrl, token)

            logger.debug("Ignoring SSL certificate errors...")
            api.setIgnoreCertificateErrors(true)
        } catch (Exception exception) {
            logger.warn("An error occurred during Gitlab API instantiation. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
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
            logger.debug("Found project ${project.nameWithNamespace}")

            return project
        } catch (Exception exception) {
            logger.warn("Could not find project ${namespace}/${projectName}. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
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
            logger.debug("Found project ${project.nameWithNamespace}")

            return project.getSshUrlToRepo()
        } catch (Exception exception) {
            logger.warn("Could not get SSH URL to repository for project ${namespace}/${projectName}. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
        }
    }

    String getRepoWebUrl(String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            logger.debug("Found project ${project.nameWithNamespace}")

            return project.getWebUrl()
        } catch (Exception exception) {
            logger.warn("Could not get WEB URL to repository for project ${namespace}/${projectName}. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
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
    synchronized String getFileContents(String filePath, String ref, String namespace, String projectName) {
        Project project = findProject(namespace, projectName)
        logger.debug("Found project ${project.nameWithNamespace}")

        return api.getRepositoryFileApi().getFile(project, filePath, ref, true).getDecodedContentAsString()
    }

//    /**
//     * Get the hash of the latest commit on the 'develop' branch
//     * !!! WARNING !!! This method was deprecated because it takes to long to get the desired commit id from big
//     * sized repositories
//     *
//     * @param namespace the projectNamespace of the project
//     * @param projectName the projectName of the project
//     * @return the SHA-1 hash of the identified commit
//     * @throws Exception if the Project does not contains a 'develop' named branch
//     */
//    @Override
//    synchronized String getDevelopBranchLatestCommitSha(String namespace, String projectName) {
//
//        Project project = findProject(namespace, projectName)
//
//        Optional<Commit> devLatestCommit = Optional.of(api.getCommitsApi().getCommit(project, DEVELOP_BRANCH))
//
//        if (!devLatestCommit.isPresent())
//            throw new RuntimeException("Unable to get '$DEVELOP_BRANCH' latest commit from Project '$namespace/$projectName'")
//
//        return devLatestCommit.get().getId()
//    }

    /**
     * Get the hash of the latest commit on the specific branch
     *
     * @param namespace
     * @param projectName
     * @return the SHA-1 hash of the identified commit
     * @throws Exception if the Project does not contains a specific named branch
     */
    @Override
    synchronized String getSpecificBranchLatestCommitSha(String namespace, String projectName, String branchName) {
        Project project = findProject(namespace, projectName)
        logger.debug("Found project ${project.nameWithNamespace}")
        String commitId

        try {
            commitId = getLastCommitShaFromBranch(project, branchName)

            if (!commitId)
                throw new RuntimeException("Unable to fetch commits for ref '$branchName'")

        } catch (Exception exception) {
            logger.warn("Unable to get the id of the latest commit within '$branchName' ref. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
        }

        logger.debug("Found latest commit ${commitId}")
        return commitId
    }

    /**
     * Get the hash of the latest commit on the 'develop' or 'main' branch
     *
     * @param namespace
     * @param projectName
     * @return the SHA-1 hash of the identified commit
     * @throws Exception if the Project does not contains a 'develop' or 'main' named branch
     */
    @Override
    synchronized String getDevelopBranchLatestCommitSha(String namespace, String projectName) {
        Project project = findProject(namespace, projectName)
        logger.debug("Found project ${project.nameWithNamespace}")
        String commitId

        try {
            commitId = getLastCommitShaFromBranch(project, DEVELOP_BRANCH)

            if (!commitId){
                commitId = getLastCommitShaFromBranch(project, NEXT_BRANCH)
            }

            if (!commitId)
                throw new RuntimeException("Unable to fetch commits for ref '$DEVELOP_BRANCH' or from $NEXT_BRANCH")

        } catch (Exception exception) {
            logger.warn("Unable to get the id of the latest commit within '$DEVELOP_BRANCH' ref. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
        }

        logger.debug("Found latest commit ${commitId}")
        return commitId
    }
    private String getLastCommitShaFromBranch(Project aProject, String aBranch){
        logger.info("Trying to get the latest commit within '$aBranch' for project ${aProject.getNamespace().getName()}/${aProject.getName()}")

        String[] curlCmd = ["curl", "--insecure", "-H", "PRIVATE-TOKEN: $token", "$repoUrl/api/v4/projects/${aProject.id}/repository/commits?ref_name=$aBranch"]
        logger.debug("Executing `curl` command: ${curlCmd.toString()}")

        String response = curlCmd.execute().text

        return new JsonSlurper().parseText(response)[0]?.id
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
    synchronized String getTagHash(String tagName, String namespace, String projectName) {
        try {
            Project project = findProject(namespace, projectName)
            logger.debug("Found project ${project.nameWithNamespace}")

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)
            Tag tag = tags.filter({ tag -> tag.getName().endsWith(tagName) }).findFirst().get()

            if (!Optional.of(tag.getCommit()).isPresent()) {
                throw new RuntimeException("Unable to get commit from Tag '${tag.name}' of Project ${namespace}/${projectName}")
            }

            logger.debug("Found tag ${tag.getName()}")
            return tag.getCommit().getId()
        } catch (Exception exception) {
            logger.warn("Could not get tag ${tagName} hash. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
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
    synchronized List<String> listTags(String namespace, String projectName, Predicate<? super Tag> predicate) {
        try {
            Project project = findProject(namespace, projectName)
            logger.debug("Found project ${project.nameWithNamespace}")

            Stream<Tag> tags = api.getTagsApi().getTagsStream(project)
            return tags
                    .filter { tag -> SemanticVersion.isValid(tag.getName()) }
                    .filter(predicate)
                    .collect { tag -> SemanticVersion.create(tag.getName()).getVersion()}

        } catch (Exception exception) {
            logger.warn("Could not get tags for project ${projectName}. Cause: ${exception.getMessage()}", exception)
            logger.debug("Exception thrown:", exception)
            throw exception
        }
    }
}
