package com.asseco.pst.devops.infrastructure

class DependenciesCrawler extends Worker {


    DependenciesCrawler(Project aProject) {
        super(aProject)
    }

    @Override
    void run() {
        minion.checkDependencies()
    }
}
