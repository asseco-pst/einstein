package com.asseco.pst.devops.infrastructure

import java.nio.file.Path
import java.nio.file.Paths

abstract class Worker implements Runnable {

    Minion minion
    Project project
    File workspaceFolder

    Worker(Project aProject) {
        setWorkspace()

        project = aProject
        minion = new Minion(project, workspaceFolder)

    }

    void setWorkspace() {

        workspaceFolder = new File("workspace")
        if(!workspaceFolder.exists())
            workspaceFolder.mkdirs()
    }
}
