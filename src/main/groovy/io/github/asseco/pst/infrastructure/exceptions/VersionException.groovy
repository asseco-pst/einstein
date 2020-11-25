package io.github.asseco.pst.infrastructure.exceptions

class VersionException extends RuntimeException {
    VersionException(String s) {
        super(s)
    }

    VersionException(String s, Throwable throwable) {
        super(s, throwable)
    }
}
