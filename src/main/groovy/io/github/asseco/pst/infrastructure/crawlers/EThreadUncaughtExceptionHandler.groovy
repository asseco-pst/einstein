package io.github.asseco.pst.infrastructure.crawlers

class EThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    boolean hasUncaughtExceptions
    Throwable threadTrowable

    private Worker worker

    EThreadUncaughtExceptionHandler(Worker aWorker) {
        worker = aWorker
        worker.setUncaughtExceptionsHandler(this)
        hasUncaughtExceptions = false
    }

    @Override
    void uncaughtException(Thread thread, Throwable throwable) {

        hasUncaughtExceptions = true
        threadTrowable = throwable

        worker.update()
    }
}
