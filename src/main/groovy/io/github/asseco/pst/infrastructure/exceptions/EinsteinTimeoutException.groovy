package io.github.asseco.pst.infrastructure.exceptions

import io.github.asseco.pst.infrastructure.utils.EinsteinProperties

class EinsteinTimeoutException extends RuntimeException {

    EinsteinTimeoutException(String var1) {
        super("The process exceeded the timeout of " + EinsteinProperties.instance().getMaxDuration() + " seconds...")
    }
}
