package io.github.asseco.pst.infrastructure.metrics

import groovy.time.TimeDuration
import io.github.asseco.pst.infrastructure.utils.Console

class Metrics {

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
        Console.debug("Start tracking timer for metric $category...")
        timer.start()
    }

    void stopTimeTracking() {
        Console.debug("Stop tracking timer for metric $category...")
        timer.stop()
    }

    int getTimelapse() {
        return timer.timelapse()
    }

    TimeDuration getTimeDuration() {
        return timer.duration()
    }
}
