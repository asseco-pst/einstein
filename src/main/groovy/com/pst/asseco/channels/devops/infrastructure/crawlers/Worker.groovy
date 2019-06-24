package com.pst.asseco.channels.devops.infrastructure.crawlers


import com.pst.asseco.channels.devops.infrastructure.utils.Console
import com.pst.asseco.channels.devops.Main

abstract class Worker extends Thread implements Observer, Observable {

    protected String _id

    protected List<Observer> observers
    protected synchronized int currentNbrOfSubscribedMinions

    Worker() {

        observers = []
        currentNbrOfSubscribedMinions = 0
    }

    protected void setId(String aId) {
        _id = aId
    }

    protected abstract void work()

    @Override
    void run() {

        try {
            work()
            wait4SubscribedMinions()
            _notify()
        } catch (Exception e) {
            Console.debug("An error occured on Thread '$_id'")
            Main.interrupt(e)
        }
    }

    @Override
    void attach(Observer aObserver) {
        observers << aObserver
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

    protected updateCurrentNbrOfSubscribedMinions(int aVal) {
        currentNbrOfSubscribedMinions += aVal
//        Console.debug("Worker '$_id' is following $currentNbrOfSubscribedMinions minions...")
    }

    protected void wait4SubscribedMinions() {

        if (!currentNbrOfSubscribedMinions)
            return

//        Console.debug("Worker $_id is waiting for $currentNbrOfSubscribedMinions minions...!")
        while (currentNbrOfSubscribedMinions) {
            // wait for minions to finish their jobs...
            print "" // do not remove. Otherwise, for some (unknown) reason, the script "runs forever"...
        }
    }
}
