package io.github.asseco.pst.infrastructure.metrics

import groovy.time.TimeDuration
import io.github.asseco.pst.infrastructure.logs.LoggerFactory
import org.slf4j.Logger

class Metrics {
    private static final Logger logger = LoggerFactory.getLogger(Metrics.class)
    enum Category {
        DEPENDENCIES_CALCULATION_DURATION
    }

    Timer timer
    Category category

    Metrics(Category aCategory) {
        timer = new Timer()
        category = aCategory
    }

    void startTimeTracking() {
        logger.debug("Start tracking timer for metric ${category}...")
        timer.start()
    }

    void stopTimeTracking() {
        timer.stop()
        logger.debug("Stop tracking timer for metric ${category}. It took ${timer.timelapse()} seconds.")
    }

    int getTimelapse() {
        return timer.timelapse()
    }

    TimeDuration getTimeDuration() {
        return timer.duration()
    }
}
