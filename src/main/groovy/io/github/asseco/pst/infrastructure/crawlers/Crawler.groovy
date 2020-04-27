package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.Project

import java.nio.file.Path
import java.nio.file.Paths

abstract class Crawler extends Worker {

    protected final String WORKSPACE_FOLDER = "crawlers-workspace"

    protected Project project
    protected File workspaceFolder

    Crawler(DependenciesHandler aDepsHandler, Project aProject) {
        super(aDepsHandler)

        project = aProject
        setWorkspace()
        setId("$project.name:${project.version.toString()}")
    }

    void setWorkspace() {

        Path workspaceFolderPath = Paths.get([Einstein.instance.getWorkspaceFolder(), WORKSPACE_FOLDER].join("/"))
        workspaceFolder = new File(workspaceFolderPath.toString())

        if (!workspaceFolder.exists())
            workspaceFolder.mkdirs()
    }

    protected void storeFile() {
        new FileStorer(project, workspaceFolder).start()
    }
}
