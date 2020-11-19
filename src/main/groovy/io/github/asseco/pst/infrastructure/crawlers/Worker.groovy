package io.github.asseco.pst.infrastructure.crawlers

import io.github.asseco.pst.infrastructure.DependenciesHandler
import io.github.asseco.pst.infrastructure.Einstein
import io.github.asseco.pst.infrastructure.exceptions.EThreadUncaughtExceptionHandler
import io.github.asseco.pst.infrastructure.exceptions.EinsteinTimeoutException

import java.util.concurrent.atomic.AtomicInteger

abstract class Worker implements Runnable, Observer, Observable {

    protected String _id
    protected DependenciesHandler depsHandler

    synchronized List<Worker> observers
//    protected synchronized int currentNbrOfSubscribedMinions
    protected AtomicInteger currentNbrOfSubscribedMinions
    EThreadUncaughtExceptionHandler uncaughtExceptionHandler

    Worker(DependenciesHandler aDepsHandler) {
        depsHandler = aDepsHandler
        observers = []
//        currentNbrOfSubscribedMinions = 0
        currentNbrOfSubscribedMinions = new AtomicInteger(0)
    }

    protected abstract void work()

    protected void setId(String aId) {
        _id = aId
    }

//    void setDependenciesHandler(DependenciesHandler aDepsHandler) {
//        depsHandler = aDepsHandler
//    }

    @Override
    void run() {

//            try {
                work()
                wait4SubscribedMinions()
                checkUncaughtExceptions()
                _notify()
//            } catch (Exception e) {
//                throw e
//            }
        work()
        wait4SubscribedMinions()
        _notify()
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

    void setUncaughtExceptionsHandler(EThreadUncaughtExceptionHandler aUncaughtExceptionsHandler) {
        uncaughtExceptionHandler = aUncaughtExceptionsHandler
    }

    private void checkUncaughtExceptions() {

        if(uncaughtExceptionHandler) {
            if(uncaughtExceptionHandler.hasUncaughtExceptions)
                throw new RuntimeException(uncaughtExceptionHandler.threadTrowable)
        }
    }

    protected updateCurrentNbrOfSubscribedMinions(int aVal) {

//        currentNbrOfSubscribedMinions += aVal
        currentNbrOfSubscribedMinions.getAndAdd(aVal)
//        if(currentNbrOfSubscribedMinions < 0)
//            currentNbrOfSubscribedMinions = 0
    }

    protected void wait4SubscribedMinions() {

        if (!currentNbrOfSubscribedMinions)
            return

        while (currentNbrOfSubscribedMinions.get() > 0) {
            // wait for minions to finish their job... until timeout
            if(Einstein.instance.timeout())
                throw new EinsteinTimeoutException()
        }
    }
}