package io.github.asseco.pst.infrastructure

import io.github.asseco.pst.http.GitLabRepositoryExplorer
import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.exceptions.VersionException
import io.github.asseco.pst.infrastructure.utils.SemanticVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

class Project {
    private static final Logger logger = LoggerFactory.getLogger(Project.class)
    static final String EINSTEIN_FILENAME = "einstein.yaml"

    String name
    SemanticVersion version
    String namespace
    String parentProjectRef // identifies the parent project by: projectNamespace, projectName and version
    String id // identifies the Project by: projectNamespace and projectName
    String ref // identifies the Project by: projectNamespace, projectName and version
    String repoSshUrl
    String repoHttpsUrl
    String versionCommitSha
    List<Project> dependencies
    String einsteinFileContent

    private Project() {
        dependencies = []
    }

    synchronized static Project factory(String aNamespace, String aName, String aVersion, String aParentProjectRef = "") {
        Project project

        try {
            project = new Builder()
                    .setParentProjectRef(aParentProjectRef)
                    .setNamespace(aNamespace)
                    .setName(aName)
                    .setVersion(aVersion)
                    .build()

        } catch (exception) {
            logger.warn("Unable to instantiate Project '${aNamespace}/${aName}' for version '${aVersion}'")
            logger.debug("Exception thrown:", exception)
            throw exception
        }

        return project
    }

    /**
     * Given a Project and a version it returns the original version tag's value
     *
     * @param aProjNamespace
     * @param aProjectName
     * @param aVersion
     * @return the aVersion original tag's value
     */
    private static String getTagFromVersion(String aProjNamespace, String aProjectName, String aVersion) {
        List<String> fetchedVersions = RepoExplorerFactory.get().listTags(
                aProjNamespace,
                aProjectName,
                { tag ->
                    SemanticVersion.create(tag.getName()).isEqualTo(SemanticVersion.create(aVersion))
                }
        )

        if (!fetchedVersions) {
            throw new VersionException("Unable to get tag of version '${aVersion}' from project ${aProjNamespace}/${aProjectName}")
        }

        return fetchedVersions.first()
    }

    private void setVersionSha() {
        if (version.isSnapshot()) {
            try {
                versionCommitSha = RepoExplorerFactory.get().getDevelopBranchLatestCommitSha(namespace, name)
            } catch (Exception exception) {
                throw new VersionException("Unable to get SNAPSHOT sha commit id from Project $namespace/$name. Does " +
                        "this project contains a '${GitLabRepositoryExplorer.DEVELOP_BRANCH}' branch?", exception)
            }
        } else {
            versionCommitSha = RepoExplorerFactory.get().getTagHash(version.toString(), namespace, name)
        }
    }

    synchronized void addDependency(Project aProject) {
        dependencies.add(aProject)
    }

    void loadEinsteinFileContent() {
        try {
            einsteinFileContent = RepoExplorerFactory.get().getFileContents(EINSTEIN_FILENAME, versionCommitSha, namespace, name)
        } catch (exception) {
            logger.warn("Could not load contents of ${EINSTEIN_FILENAME} from project ${name}. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
        }
    }

    /**
     * Parses the contents of the requirements.yaml file and builds a list of
     * Requirement objects.
     *
     * @return list of project runtime requirements
     */
    List<Requirement> readRequirements() {
        Yaml yamlParser = new Yaml()
        Map<String, Object> parsed = yamlParser.load(einsteinFileContent)

        List<Requirement> requirements = []
        parsed.each { namespace, project ->
            List<Map> map = (List<Map>) project

            map.each {
                requirements.add(
                        new Requirement(
                                projectNamespace: namespace.trim(),
                                projectName: it.keySet().first().toString().trim(),
                                versionRange: it.values().first().toString().trim()
                        )
                )
            }
        }

        return requirements
    }

    boolean hasRequirementsFile() {
        return einsteinFileContent
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

        Builder setParentProjectRef(String aParentProjectRef) {
            project.parentProjectRef = aParentProjectRef
            return this
        }

        Builder setVersion(String aVersion) {
            project.version = SemanticVersion.create(aVersion)
            return this
        }

        Project build() {
            project.with {
                setRepoSshUrl(RepoExplorerFactory.get().getRepoSshUrl(project.namespace, project.name))
                setRepoHttpsUrl(RepoExplorerFactory.get().getRepoWebUrl(project.namespace, project.name))
                setVersionSha()
                setId("$project.namespace/$project.name")
                setRef("$project.namespace/$project.name:${project.version.toString()}")
                loadEinsteinFileContent()
            }

            return project
        }
    }
}
