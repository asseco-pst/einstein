package com.assecopst.channels.devops.infrastructure.crawlers


import com.assecopst.channels.devops.infrastructure.Project

abstract class MinionsFactory {

    enum Type {
        CRAWLER,
        VERSION_SEEKER
    }


    static void create(Type aType, Project aProject, Worker aObserver, String aDependencyLine = null) {

        Worker minion

        switch (aType) {
            case Type.CRAWLER:
                minion = new FileParserMinion(aProject)
                break
            case Type.VERSION_SEEKER:
                minion = new VersionSeekerMinion(aProject, aDependencyLine)
                break
        }

        minion.attach(aObserver)
        aObserver.updateCurrentNbrOfSubscribedMinions(1)

        minion.start()
    }
}
