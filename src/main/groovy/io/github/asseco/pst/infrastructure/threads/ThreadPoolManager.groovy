package io.github.asseco.pst.infrastructure.threads


import io.github.asseco.pst.infrastructure.crawlers.Worker
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger

import java.util.concurrent.Future
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.TimeUnit

@Singleton
class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class)
    private ObserverThreadPoolExecutor threadPoolExecutor

    void initializePool() {
        logger.debug("Initializing the thread pool executor!")
        threadPoolExecutor = new ObserverThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
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
