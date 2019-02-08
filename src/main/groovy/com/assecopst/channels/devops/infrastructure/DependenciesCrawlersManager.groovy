package com.asseco.pst.devops.infrastructure

import com.asseco.pst.devops.infrastructure.utils.Console

class DependenciesCrawlersManager {

    static List<Thread> workers
    static List<String> crawledProjects


    static {
        workers = []
        crawledProjects = []
    }


    static void calcDependencies(Project aProject) {

        String projectId = aProject.name
        if(crawledProjects.contains(projectId)) {
            Console.info("Projects '${projectId}' already crawled...")
            return
        }
        crawledProjects << projectId

        Thread t = new Thread(new DependenciesCrawler(aProject))
        workers << t

        t.start()
        t.join()
    }

//    static void waitUntilFinish() {
//
//        workers.each { thread ->
//            thread.join()
//        }
//    }

    static void interruptAll() {

        workers.each { thread ->
            thread.interrupt()
        }
    }
}
