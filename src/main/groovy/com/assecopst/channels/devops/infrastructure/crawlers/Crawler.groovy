package com.assecopst.channels.devops.infrastructure.crawlers


import com.assecopst.channels.devops.infrastructure.Project

abstract class Crawler extends Worker {

    protected final String WORKSPACE_FOLDER = "crawlers-workspace"
    protected File workspaceFolder

    protected Project project

    Crawler(Project aProject) {
        super()

        project = aProject
        setWorkspace()
        setId("$project.name:${project.version.toString()}")
    }

    void setWorkspace() {

        workspaceFolder = new File(WORKSPACE_FOLDER)

        if (!workspaceFolder.exists())
            workspaceFolder.mkdirs()
    }

    protected void storeFile() {
        new FileStorer(project, workspaceFolder).start()
    }
}
