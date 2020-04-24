package io.github.asseco.pst.infrastructure.metrics

import groovy.time.TimeDuration
import io.github.asseco.pst.infrastructure.utils.Console

@Singleton
class Metrics {

    enum METRIC {
        DEPENDENCIES_CALCULATION_DURATION
    }

    Map<METRIC, Object> timers = [:]

//    Metrics() {
//        timers = [:]
//    }

    void startTimeTracking(METRIC aMETRIC) {

        Console.debug("Start tracking timer for metric $aMETRIC...")
        Timer timer = new Timer()
        timers[aMETRIC] = timer

        timer.start()
    }

    void stopTimeTracking(METRIC aMetric) {

        checkIfExistsTimer(aMetric)

        Console.debug("Stop tracking timer for metric $aMetric...")
        ((Timer) timers[aMetric]).stop()
    }

    int getTimelapse(METRIC aMetric) {

        checkIfExistsTimer(aMetric)
        return ((Timer) timers[aMetric]).timelapse()
    }

    TimeDuration getTimeDuration(METRIC aMetric) {

        checkIfExistsTimer(aMetric)
        return ((Timer) timers[aMetric]).duration()
    }

    private void checkIfExistsTimer(METRIC aMetric) {
        if (!timers.containsKey(aMetric))
            throw new Exception("There's no timer set for Metric ${aMetric.toString()}")
    }
}
