package com.assecopst.channels.devops.infrastructure.crawlers

interface Observable {

    void attach(Observer aObserver)

    void _notify()
}
