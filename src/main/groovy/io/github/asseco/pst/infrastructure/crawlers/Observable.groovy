package io.github.asseco.pst.infrastructure.crawlers

interface Observable {
    void attach(Observer aObserver)
    void _notify()
}
