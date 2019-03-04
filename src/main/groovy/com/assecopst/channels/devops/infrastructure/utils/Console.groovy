package com.assecopst.channels.devops.infrastructure.utils

import com.assecopst.channels.devops.infrastructure.Einstein

class Console {

    static void print(def aMsg) {
        info(aMsg)
    }

    static void info(def aMsg) {
        println "${time()} - [INFO] $aMsg"
    }

    static void warn(String aMsg) {
        println "${time()} - [WARNING] $aMsg"
    }

    static void err(String aMsg) {
        println "${time()} - [ERROR] $aMsg"
    }

    static void debug(String aMsg) {
        if (Einstein.isDebugModeOn())
            println "${time()} - [DEBUG] $aMsg"
    }

    private static Date time() {
        return new Date()
    }
}
