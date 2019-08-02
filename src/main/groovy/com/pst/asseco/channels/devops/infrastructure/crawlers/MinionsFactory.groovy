package com.pst.asseco.channels.devops.infrastructure.crawlers


import com.pst.asseco.channels.devops.infrastructure.Project
import com.pst.asseco.channels.devops.infrastructure.Requirement

abstract class MinionsFactory {

    enum Type {
        CRAWLER,
        VERSION_SEEKER
    }


    static void create(Type aType, Project aProject, Worker aObserver, Requirement requirement = null) {

        Worker minion

        switch (aType) {
            case Type.CRAWLER:
                minion = new FileParserMinion(aProject)
                break
            case Type.VERSION_SEEKER:
                minion = new VersionSeekerMinion(aProject, requirement)
                break
        }

        minion.attach(aObserver)
        aObserver.updateCurrentNbrOfSubscribedMinions(1)

        minion.start()
    }
}
