package com.pst.asseco.channels.devops.infrastructure.metrics

class Metrics {

    enum METRIC {
        DEPENDENCIES_CALCULATION_DURATION
    }

    Map<METRIC, Object> timers

    Metrics() {
        timers = [:]
    }

    void startTimeTracking(METRIC aMETRIC) {

        Timer timer = new Timer()
        timers[aMETRIC] = timer

        timer.start()
    }

    void stopTimeTracking(METRIC aMetric) {

        checkIfExistsTimer(aMetric)
        ((Timer) timers[aMetric]).stop()
    }

    def getTimeDuration(METRIC aMetric) {

        checkIfExistsTimer(aMetric)
        return ((Timer) timers[aMetric]).duration()
    }

    private void checkIfExistsTimer(METRIC aMetric) {
        if (!timers.containsKey(aMetric))
            throw new Exception("There's no timer set for Metric ${aMetric.toString()}")
    }
}
