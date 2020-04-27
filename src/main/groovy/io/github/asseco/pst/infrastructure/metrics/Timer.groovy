package io.github.asseco.pst.infrastructure.metrics

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import io.github.asseco.pst.infrastructure.utils.Console

import java.text.SimpleDateFormat

class Timer {

    private Date startTime, endTime

    Timer() {}

    void start() {
        startTime = new Date()
        Console.debug("Starting timer => " + new SimpleDateFormat("YYY-MM-dd HH:mm:ssZ").format(startTime))
    }

    void stop() {
        endTime = new Date()
        Console.debug("Stopping timer => " + new SimpleDateFormat("YYY-MM-dd HH:mm:ssZ").format(endTime))
    }

    /**
     * Get the timelapse since the timer was started
     * @return timelapse in seconds
     */
    int timelapse() {
        return Math.round((TimeCategory.minus(new Date(), startTime).toMilliseconds() / 1000) as double)
    }

    /**
     * Get the total time duration of timer's measurement
     * @return duration
     */
    TimeDuration duration() {

        if (!startTime || !endTime)
            throw new Exception("You must start/stop the Timer in order to be able to calculate durations")

        Console.debug("start time: " + startTime)
        Console.debug("end time: " + endTime)
        return TimeCategory.minus(endTime, startTime)
    }
}
