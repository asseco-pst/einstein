package com.assecopst.channels.devops.infrastructure.utils


class Console {

    static void print(String aMsg) {
        println aMsg
    }

    static void info(String aMsg) {
        print "[INFO] ${aMsg}"
    }

    static void warn(String aMsg) {
        print "[WARNING] ${aMsg}"
    }

    static void err(String aMsg) {
        print "[ERROR] ${aMsg}"
    }
}
