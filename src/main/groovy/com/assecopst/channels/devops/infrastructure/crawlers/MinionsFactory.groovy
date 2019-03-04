package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.Project

abstract class MinionsFactory {

    enum Type {
        CRAWLER,
        VERSION_PARSER
    }


    static void create(Type Type, Project aProject, Worker aObserver, String aDependencyLine = null) {

        Worker minion

        switch (Type) {
            case Type.CRAWLER:
                minion = new Minion(aProject)
                break
            case Type.VERSION_PARSER:
                minion = new VersionParserMinion(aProject, aDependencyLine)
                break
        }

        minion.attach(aObserver)
        aObserver.updateCurrentNbrOfSubscribedMinions(1)

        minion.start()
    }
}
