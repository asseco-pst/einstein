package com.assecopst.channels.devops.infrastructure.crawlers

import com.assecopst.channels.devops.infrastructure.Project
import com.assecopst.channels.devops.infrastructure.utils.Console

class CrawlersManager {

    static List<Thread> workers
    static List<String> crawledProjects

    static {
        workers = []
        crawledProjects = []
    }


    static void calcDependencies(Project aProject) {

        String projectId = aProject.name
        if (crawledProjects.contains(projectId)) {
            Console.info("'${projectId}' Project was already crawled...")
            return
        }
        crawledProjects << projectId

        Thread t = new Thread(new Crawler(aProject))
        workers << t

        t.start()
        t.join()
    }

    static void interruptAll() {

        workers.each { thread ->
            thread.interrupt()
        }
    }
}
