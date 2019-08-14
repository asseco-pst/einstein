package io.github.asseco.pst.infrastructure.utils

import groovy.json.JsonBuilder
import io.github.asseco.pst.infrastructure.Einstein

class Console {

    static void print(def aMsg) {
        println aMsg
    }

    static void printMap(Map aMap) {
        println new JsonBuilder(aMap).toPrettyString()
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
