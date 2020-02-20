package io.github.asseco.pst.infrastructure.metrics

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

    /**
     * Get the timelapse since the timer was started
     * @return timelapse in seconds
     */
    int timelapse() {
        TimeCategory.minus(new Date(), startTime).getSeconds()
    }

    /**
     * Get the total time duration of timer's measurement
     * @return duration in seconds
     */
    int duration() {

        if (!startTime || !endTime)
            throw new Exception("You must start/stop the Timer before trying to get its duration")

        return TimeCategory.minus(endTime, startTime).getSeconds()
    }

}
