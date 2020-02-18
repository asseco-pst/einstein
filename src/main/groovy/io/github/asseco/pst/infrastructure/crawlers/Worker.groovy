package io.github.asseco.pst.infrastructure.crawlers

abstract class Worker implements Runnable, Observer, Observable {

    protected String _id

    List<Worker> observers
    protected synchronized int currentNbrOfSubscribedMinions
    protected EThreadUncaughtExceptionHandler uncaughtExceptionHandler

    Worker() {
        observers = []
        currentNbrOfSubscribedMinions = 0
//        setUncaughtExceptionHandler(new EThreadUncaughtException(this))
    }

    protected abstract void work()

    protected void setId(String aId) {
        _id = aId
    }

    @Override
    void run() {

//        try {

            work()
            wait4SubscribedMinions()
            _notify()

//            checkUncaughtExceptions()

//        } catch (Exception e) {
//            Console.debug("An error occurred on Thread '$_id'")
//            checkUncaughtExceptions()
//        }
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

//        Console.debug("Worker $_id is notifying its followers that its job is done!")
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
        if(currentNbrOfSubscribedMinions + aVal >= 0)
            currentNbrOfSubscribedMinions += aVal
        else
            currentNbrOfSubscribedMinions = 0
    }

    protected void wait4SubscribedMinions() {

        if (!currentNbrOfSubscribedMinions)
            return

//        Console.debug("Worker $_id is waiting for $currentNbrOfSubscribedMinions minions...!")
        while (currentNbrOfSubscribedMinions) {
            // wait for minions to finish their jobs...
            print "" // do not remove. Otherwise, for some (unknown) reason, the script "runs forever"...
        }

        checkUncaughtExceptions()
    }
}