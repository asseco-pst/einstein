package io.github.asseco.pst.infrastructure.exceptions

import io.github.asseco.pst.infrastructure.crawlers.Worker

@Singleton
class UncaughtExceptionsManager {

    List<EThreadUncaughtExceptionHandler> uncaughtExceptions = []

    synchronized EThreadUncaughtExceptionHandler factory(Worker aObserver) {

        EThreadUncaughtExceptionHandler uncaughtExceptionHandler = new EThreadUncaughtExceptionHandler(aObserver)
        uncaughtExceptions << uncaughtExceptionHandler

        return uncaughtExceptionHandler
    }

    /**
     * If a uncaught exception is found main thread is interrupted by throwing the first identified uncaught exception
     */
    void checkUncaughtExceptions() {

        uncaughtExceptions.each {
            if(it.hasUncaughtExceptions)
                throw it.threadTrowable
        }
    }
}
