package io.github.asseco.pst.infrastructure.threads

import io.github.asseco.pst.infrastructure.exceptions.UncaughtExceptionsManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadFactory

class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionsManager.class)

    @Override
    void uncaughtException(Thread t, Throwable e) {
        if (e != null) {
            logger.debug(e.message)
            UncaughtExceptionsManager.instance.uncaughtExceptions.add(e)
        }
    }
}

class EThreadFactory implements ThreadFactory {
    @Override
    Thread newThread(Runnable r) {
        Thread t = new Thread(r)
        t.setUncaughtExceptionHandler(new UncaughtExceptionHandler())
        return t
    }
}
