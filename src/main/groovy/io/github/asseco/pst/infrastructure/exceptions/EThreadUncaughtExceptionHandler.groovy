package io.github.asseco.pst.infrastructure.exceptions

import io.github.asseco.pst.infrastructure.crawlers.Worker

class EThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    boolean hasUncaughtExceptions
    Throwable threadTrowable

    private Worker observer

    EThreadUncaughtExceptionHandler(Worker aObserver) {
        observer = aObserver
        observer.setUncaughtExceptionsHandler(this)
        hasUncaughtExceptions = false
    }

    @Override
    void uncaughtException(Thread thread, Throwable throwable) {

        hasUncaughtExceptions = true
        threadTrowable = throwable

        observer.update()
    }
}
