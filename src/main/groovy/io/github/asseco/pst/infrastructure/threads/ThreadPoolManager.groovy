package io.github.asseco.pst.infrastructure.threads


import io.github.asseco.pst.infrastructure.crawlers.Worker
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Future
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit

@Singleton
class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class)
    private ObserverThreadPoolExecutor threadPoolExecutor

    void initializePool() {
        if (threadPoolExecutor && !threadPoolExecutor.isShutdown()) {
            logger.debug("Thread pool executor already initialized!")
            return
        }

        logger.debug("Initializing the thread pool executor!")
        threadPoolExecutor = new ObserverThreadPoolExecutor(
                35, Integer.MAX_VALUE,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new EThreadFactory()
        )
    }

    void executeWorker(Worker worker) {
        logger.debug("Launching worker ${worker.getClass().toGenericString()}")
        threadPoolExecutor.execute(worker)
    }

    Future<?> submitWorker(Worker worker) {
        logger.debug("Submitting worker ${worker.getClass().toGenericString()}")
        threadPoolExecutor.submit(worker)
    }

    void shutdownPool() {
        if(!threadPoolExecutor || threadPoolExecutor.isShutdown()) {
            logger.debug("Thread pool executor not initialized or already shutdown!")
            return
        }

        logger.debug("Shutting down thread pool executor!")
        threadPoolExecutor.shutdown() // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow() // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS))
                    logger.error("Thread pool executor did not terminate!")
            }
        } catch (InterruptedException ignore) {
            // (Re-)Cancel if current thread also interrupted
            threadPoolExecutor.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }
}
