package com.assecopst.channels.devops

import com.assecopst.channels.devops.infrastructure.Application
import com.assecopst.channels.devops.infrastructure.DB
import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.utils.Console

class Main {
    static void main(String[] args) {

        List<Project> projects = []

        try {

            projects <<
                    new Project.Builder()
                            .setName("a")
                            .setVersion("1.7.0")
                            .setRepoHttpsUrl(DB.Repos.A.httpsUrl)
                            .setRepoSshUrl(DB.Repos.A.sshUrl)
                            .build()

        } catch (e) {
            Console.err "An error occurred when trying to instantiate Projects. Cause: ${e}"
        }

        Application.calcDependencies(projects)
        println "Finished!"
    }
}
