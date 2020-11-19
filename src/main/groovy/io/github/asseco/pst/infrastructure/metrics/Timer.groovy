package io.github.asseco.pst.infrastructure.metrics

import groovy.time.TimeCategory
import groovy.time.TimeDuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

class Timer {

    private static final Logger logger = LoggerFactory.getLogger(Timer.class)
    private Date startTime, endTime

    Timer() {}

    void start() {
        startTime = new Date()
        logger.debug("Starting timer => " + new SimpleDateFormat("YYY-MM-dd HH:mm:ssZ").format(startTime))
    }

    void stop() {
        endTime = new Date()
        logger.debug("Stopping timer => " + new SimpleDateFormat("YYY-MM-dd HH:mm:ssZ").format(endTime))
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

        logger.debug("start time: " + startTime)
        logger.debug("end time: " + endTime)
        return TimeCategory.minus(endTime, startTime)
    }
}
