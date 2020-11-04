package io.github.asseco.pst.infrastructure.utils

import io.github.asseco.pst.infrastructure.crawlers.Worker

class ThreadsManager {
    synchronized List<Thread> liveThreads = []

    Thread newThread(Worker aWorker) {
        Thread t = new Thread(aWorker)
        liveThreads << t

        return t
    }

    void killLiveThreads() {
        liveThreads.each { t ->
            t.interrupt()
        }
    }
}
