package com.assecopst.channels.devops.infrastructure.utils


class Console {

    static void print(def aMsg) {
        println aMsg
    }

    static void info(def aMsg) {
        print "${aMsg.toString()}"
    }

    static void warn(String aMsg) {
        print "[WARNING] ${aMsg}"
    }

    static void err(String aMsg) {
        print "[ERROR] ${aMsg}"
    }
}
