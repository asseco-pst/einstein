package io.github.asseco.pst.infrastructure.crawlers


import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.Requirement

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

        Thread t = new Thread(minion)
        t.setUncaughtExceptionHandler(new EThreadUncaughtExceptionHandler(aObserver))
        t.start()
    }
}
