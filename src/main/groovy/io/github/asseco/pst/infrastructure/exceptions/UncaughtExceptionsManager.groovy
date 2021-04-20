package io.github.asseco.pst.infrastructure.exceptions

import io.github.asseco.pst.infrastructure.crawlers.Worker
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger

@Singleton
class UncaughtExceptionsManager {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionsManager.class)
    List<EThreadUncaughtExceptionHandler> uncaughtExceptions = []

    synchronized EThreadUncaughtExceptionHandler factory(Worker aObserver) {

        EThreadUncaughtExceptionHandler uncaughtExceptionHandler = new EThreadUncaughtExceptionHandler(aObserver)
        uncaughtExceptions << uncaughtExceptionHandler

        return uncaughtExceptionHandler
    }

    void reset() {
        uncaughtExceptions = []
    }

    /**
     * If a uncaught exception is found main thread is interrupted by throwing the first identified uncaught exception
     */
    void checkUncaughtExceptions() {

        uncaughtExceptions.each {
            if(it.hasUncaughtExceptions) {
                logger.error("The following exception was thrown during the dependencies calculation: "
                        + "Message: " + it.threadTrowable.getMessage()
                            + "Cause: " + it.threadTrowable.getCause())

                throw it.threadTrowable
            }
        }
    }
}
