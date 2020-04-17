package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.Requirement

abstract class MinionsFactory {

    enum Type {
        CRAWLER,
        VERSION_SEEKER
    }

    private static List<Thread> liveThreads

    static {
        liveThreads = []
    }

    private static Thread newThread(Worker aWorker) {

        Thread t = new Thread(aWorker)
        liveThreads << t

        return t
    }

    static void killLiveThreads() {

        liveThreads.each {
            it.stop()
        }
    }

    static void launch(Type aType, Project aProject, Worker aObserver, DependenciesHandler aDepsHandler, Requirement aRequirement = null) {

        Worker minion

        switch (aType) {
            case Type.CRAWLER:
                minion = new FileParserMinion(aProject)
                break
            case Type.VERSION_SEEKER:
                minion = new VersionSeekerMinion(aProject, aRequirement)
                break
        }

        minion.attach(aObserver)
        minion.setDependenciesHandler(aDepsHandler)

        Thread t = newThread(minion)
        t.setUncaughtExceptionHandler(new EThreadUncaughtExceptionHandler(aObserver))
        t.start()
    }
}
