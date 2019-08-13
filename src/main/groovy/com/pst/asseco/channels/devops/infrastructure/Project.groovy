package com.pst.asseco.channels.devops.infrastructure

import com.pst.asseco.channels.devops.http.RepoExplorerFactory
import com.pst.asseco.channels.devops.infrastructure.exceptions.VersionException
import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.utils.SemanticVersion
import org.yaml.snakeyaml.Yaml

class Project {

    static final String EINSTEIN_FILENAME = "einstein.yaml"

    String name
    SemanticVersion version
    String namespace
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

    static Project factory(String aNamespace, String aName, String aVersion) {

        Project project

        try {
            project =
                    new Builder()
                            .setNamespace(aNamespace)
                            .setName(aName)
                            .setVersion(aVersion)
                            .build()
        } catch (e) {
            Console.err("Unable to instantiate Project '$aNamespace/$aName}' for version '${aVersion}'")
            throw e
        }

        return project
    }

    private void setVersionSha() {
        if(version.isSnapshot()) {
            try {
                versionCommitSha = RepoExplorerFactory.get().getDevelopBranchLatestCommitSha(namespace, name)
            } catch (Exception aEx) {
                throw new VersionException("Project $namespace/$name does not contains a SNAPSHOT version", aEx)
            }
        }
        else
            versionCommitSha = RepoExplorerFactory.get().getTagHash(version.toString(), namespace, name)
    }

    void addDependency(Project aProject) {
        dependencies.add(aProject)
    }

    void loadEinsteinFileContent() {

        try {
            einsteinFileContent = RepoExplorerFactory.get().getFileContents(EINSTEIN_FILENAME, versionCommitSha, namespace, name)
        } catch (e) {
            Console.warn("Could not load contents of $EINSTEIN_FILENAME from project $name. Cause: $e")
        }
    }

    /**
     * Parses the contents of the requirements.yaml file and builds a list of
     * Requirement objects.
     *
     * @return list of project runtime requirements
     */
    List<Requirement> readRequirements(){

        Yaml yamlParser = new Yaml()
        Map<String, Object> parsed = yamlParser.load(einsteinFileContent)
        List<Requirement> requirements = []

        parsed.each{ namespace, project ->

            List<Map> m = (List<Map>) project

            m.each { it ->
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

        Builder setVersion(String aVersion) {
            project.version = SemanticVersion.create(aVersion)
            return this
        }

        Project build() {

            project.setRepoSshUrl(RepoExplorerFactory.get().getRepoSshUrl(project.namespace, project.name))
            project.setRepoHttpsUrl(RepoExplorerFactory.get().getRepoWebUrl(project.namespace, project.name))
            project.setVersionSha()
            project.setId("$project.namespace/$project.name")
            project.setRef("$project.namespace/$project.name:${project.version.toString()}")
            project.loadEinsteinFileContent()

            return project
        }
    }
}
