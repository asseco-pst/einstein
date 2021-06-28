package io.github.asseco.pst.infrastructure.exceptions


import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class UncaughtExceptionsManager {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionsManager.class)
    synchronized List<Throwable> uncaughtExceptions = new ArrayList<Throwable>()

    void reset() {
        uncaughtExceptions.clear()
    }

    /**
     * If a uncaught exception is found main thread is interrupted by throwing the first identified uncaught exception
     */
    void checkUncaughtExceptions() {
        if (uncaughtExceptions.size() > 0) {
            logger.error("""
                        The following exception was thrown during the dependencies calculation:
                        Message: ${uncaughtExceptions.first().getMessage()}
                        Cause: ${uncaughtExceptions.first().getCause()}
                        """)
            throw uncaughtExceptions.first()
        }
    }
}
