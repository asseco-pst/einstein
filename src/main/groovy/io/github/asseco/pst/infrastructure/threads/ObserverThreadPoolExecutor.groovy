package io.github.asseco.pst.infrastructure.threads

import java.util.concurrent.*

class ObserverThreadPoolExecutor extends ThreadPoolExecutor {
    ObserverThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue)
    }

    ObserverThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory)
    }

    ObserverThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler)
    }

    ObserverThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable)
        if (runnable instanceof io.github.asseco.pst.infrastructure.crawlers.Worker) {
            runnable._notify()
        }
    }
}
