package com.pst.asseco.channels.devops.infrastructure.crawlers


import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.infrastructure.Project

/**
 * This thread is responsible to store, on a given location,
 * the 'requirements.txt' file of a given Project
 *
 * @throws Exception in case it's not able to save the required file
 */
class FileStorer extends Thread {

    File workspaceFolder
    Project project

    FileStorer(Project aProject, File aWorkspaceFolder) {
        project = aProject
        workspaceFolder = aWorkspaceFolder
    }

    @Override
    void run() {

        storeFile()
    }


    private void storeFile() {

        String filename = "${Project.REQUIREMENTS_FILE}-${project.getVersion().toString()}-${project.versionCommitSha}"
        Console.print("Storing ${filename} of Project ${project.name}")

        try {
            File projectFolder = new File(workspaceFolder, project.name)
            projectFolder.mkdir()

            File requirements = new File(projectFolder, filename)
            requirements.write(project.requirementsFileContent)

            Console.print("File '${filename}' from '${project.name}' Project successfully stored into ${requirements.getPath()}")
        } catch (e) {
            throw new Exception("Unable to store requirements file for Project '${project.name}'. Cause: ${e}")
        }
    }
}
