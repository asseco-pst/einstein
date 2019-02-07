package com.asseco.pst.devops.infrastructure

import groovy.transform.Synchronized

class DependenciesCrawlersManager {

    static List<Thread> workers

    static {
        workers = []
    }


    static Thread calcDependencies(Project aProject) {

        Thread t
        workers << (t = new Thread(new DependenciesCrawler(aProject)))
        t.start()

        return t
    }

    static synchronized void waitUntilFinish() {

        workers.each { thread ->
            thread.join()
        }
    }

    static synchronized void interruptAll() {

        workers.each { thread ->
            thread.interrupt()
        }
    }
}
