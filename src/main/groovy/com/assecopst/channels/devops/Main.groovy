package com.asseco.pst.devops

import com.asseco.pst.devops.infrastructure.DB
import com.asseco.pst.devops.infrastructure.Project

import com.asseco.pst.devops.infrastructure.Application

class Main {
    static void main(String[] args) {


        List<Project> projects = []

        try {
            projects << new Project("a", "1.3.3", DB.Repos.A.httpsUrl, DB.Repos.A.sshUrl)
//            projects << new Project("b", "2.0.0", DB.Repos.B.httpsUrl, DB.Repos.B.sshUrl)
//            projects << new Project("c", "1.2.0", DB.Repos.C.httpsUrl, DB.Repos.C.sshUrl)
        } catch (e) {
            println "An error occurred when trying to instantiate Projects. Cause: ${e}"
        }

        Application.instance.calcDependencies(projects)
        println "Finished!"
    }
}
