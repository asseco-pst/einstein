package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.Project

abstract class Worker implements Runnable {

    private final String WORKSPACE_FOLDER = "threads-workspace"
    Project project
    File workspaceFolder

    Worker(Project aProject) {
        setWorkspace()
        project = aProject
    }

    void setWorkspace() {

        workspaceFolder = new File(WORKSPACE_FOLDER)
        if (!workspaceFolder.exists())
            workspaceFolder.mkdirs()
    }
}
