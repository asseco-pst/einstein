package com.pst.asseco.channels.devops.infrastructure

import com.pst.asseco.channels.devops.http.RepoExplorerFactory
import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.version.Version
import org.yaml.snakeyaml.Yaml

class Project {

    static final String REQUIREMENTS_FILE = "requirements.yaml"

    String name
    Version version
    String namespace
    String id // identifies the Project by: namespace and name
    String ref // identifies the Project by: namespace, name and version
    String repoSshUrl
    String repoHttpsUrl
    String versionCommitSha
    List<Project> dependencies
    String requirementsFileContent

    private Project() {
        dependencies = []
    }

    static Project factory(String aNamespace, String aName, String aVersion) {

        Project project

        try {
            project =
                    new Project.Builder()
                            .setNamespace(aNamespace)
                            .setName(aName)
                            .setVersion(aVersion)
                            .build()
        } catch (e) {
            Console.err("Unable to instantiate Project with name '${aName}' and version '${aVersion}'")
            throw e
        }

        return project
    }

    void addDependency(Project aProject) {
        dependencies.add(aProject)
    }

    void loadRequirementsFileContent() {

        try {
            requirementsFileContent = RepoExplorerFactory.get().getFileContents(REQUIREMENTS_FILE, versionCommitSha, namespace, name)
        } catch (e) {
            Console.warn("Could not load contents of $REQUIREMENTS_FILE from project $name. Cause: $e")
        }
    }

    List<Requirement> readRequirements(){
        Yaml yamlParser = new Yaml()
        Map<String, Object> parsed = yamlParser.load(requirementsFileContent)
        List<Requirement> requirements = []

        parsed.each{ namespace, project ->
            println namespace
            List<Map> m = (List<Map>) project
            m.each { it ->
                requirements.add(new Requirement(
                        namespace: namespace,
                        name: it.keySet().first().toString(),
                        version: it.values().first().toString())
                )
            }
        }

        return requirements
    }

    boolean hasRequirementsFile() {
        return requirementsFileContent
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
            project.version = Version.factory(aVersion)
            return this
        }

        Project build() {

            project.setRepoSshUrl(RepoExplorerFactory.get().getRepoSshUrl(project.namespace, project.name))
            project.setRepoHttpsUrl(RepoExplorerFactory.get().getRepoWebUrl(project.namespace, project.name))
            project.versionCommitSha = RepoExplorerFactory.get().getTagHash(project.version.toString(), project.namespace, project.name)

            project.setId("$project.namespace/$project.name")
            project.setRef("$project.namespace/$project.name:${project.version.toString()}")
            project.loadRequirementsFileContent()

            return project
        }
    }
}
