package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Project
import io.github.asseco.pst.infrastructure.Requirement
import io.github.asseco.pst.infrastructure.threads.ThreadPoolManager

abstract class MinionsFactory {
    enum Type {
        CRAWLER,
        VERSION_SEEKER
    }

    synchronized static void launch(Type aType, Project aProject, Worker aObserver, DependenciesHandler aDepsHandler, Requirement aRequirement = null) {
        Worker minion

        switch (aType) {
            case Type.CRAWLER:
                minion = new FileParserMinion(aDepsHandler, aProject)
                break
            case Type.VERSION_SEEKER:
                minion = new VersionSeekerMinion(aDepsHandler, aProject, aRequirement)
                break
        }

        minion.attach(aObserver)
        ThreadPoolManager.instance.executeWorker(minion)
    }
}
