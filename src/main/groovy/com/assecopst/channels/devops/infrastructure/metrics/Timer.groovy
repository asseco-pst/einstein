package com.assecopst.channels.devops.infrastructure.metrics

import groovy.time.TimeCategory

class Timer {

    private Date startTime, endTime

    Timer() {}

    void start() {
        startTime = new Date()
    }

    void stop() {
        endTime = new Date()
    }

    def duration() {

        if (!startTime || !endTime)
            throw new Exception("You must start/stop the Timer before trying to get its duration")

        return TimeCategory.minus(endTime, startTime)
    }

}
