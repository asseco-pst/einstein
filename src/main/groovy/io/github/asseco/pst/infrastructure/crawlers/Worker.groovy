package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.exceptions.EinsteinTimeoutException

import java.util.concurrent.atomic.AtomicInteger

abstract class Worker implements Runnable, Observer, Observable {
    protected String _id
    protected DependenciesHandler depsHandler

    protected List<Worker> observers
    protected AtomicInteger currentNbrOfSubscribedMinions

    Worker(DependenciesHandler aDepsHandler) {
        depsHandler = aDepsHandler
        observers = []
        currentNbrOfSubscribedMinions = new AtomicInteger(0)
    }

    protected abstract void work()

    protected void setId(String aId) {
        _id = aId
    }

    @Override
    void run() {
        work()
        wait4SubscribedMinions()
    }

    @Override
    void attach(Observer aObserver) {
        Worker w = (Worker) aObserver
        observers << w
        w.updateCurrentNbrOfSubscribedMinions(1)
    }

    @Override
    void _notify() {
        if (!observers)
            return

        observers.each { o ->
            o.update()
        }
    }

    @Override
    void update() {
        updateCurrentNbrOfSubscribedMinions(-1)
    }

    protected updateCurrentNbrOfSubscribedMinions(int aVal) {
        currentNbrOfSubscribedMinions.getAndAdd(aVal)
    }

    protected void wait4SubscribedMinions() {
        if (!currentNbrOfSubscribedMinions) {
            return
        }

        while (currentNbrOfSubscribedMinions.get() > 0) {
            // wait for minions to finish their job... until timeout
            if (depsHandler.getEinstein().timeout())
                throw new EinsteinTimeoutException()
        }
    }
}
