package com.asseco.pst.devops.infrastructure.utils


class Console {

    static void print(String aMsg) {
        println aMsg
    }

    static void warn(String aMsg) {
        print "[WARNING] ${aMsg}"
    }

    static void err(String aMsg) {
        print "[ERROR] ${aMsg}"
    }
}
