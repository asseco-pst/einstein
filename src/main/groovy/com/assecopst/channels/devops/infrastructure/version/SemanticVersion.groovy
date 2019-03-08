package com.assecopst.channels.devops.infrastructure.version

import java.util.regex.Matcher
import java.util.regex.Pattern

class SemanticVersion extends Version {

    protected SemanticVersion() {}

    SemanticVersion(String aVersionStr) {
        super(aVersionStr)
    }

    @Override
    String extractVersion(String value) {
        if((value =~ getRcRegexExp()).matches()){
            return
        }
    }

    @Override
    protected void parse() {

        major = formatNumber(tokenizedVersion[0])
        minor = formatNumber(tokenizedVersion[1])
        patch = formatNumber(tokenizedVersion[2])
    }

    @Override
    protected boolean checkIfHasMajorBreak(Version aVer1, Version aVer2) {
        return breakOnMajorFields(aVer1, aVer2)
    }

    @Override
    boolean match(String aVersion) {
        return ((Matcher) (aVersion =~ /([0-9]+\.[0-9]+\.[0-9]+)/)).matches()
    }

    @Override
    String getGitMatchVersionExp() {
        String minorPh = (minor) ? "${minor}." : ""
        return "${major}.${minorPh}*"
    }

    @Override
    String getGitMatchRcVersion() {
        return "${major}.${minor}*rc*"
    }

    @Override
    def getVersionRegexExp() {
        String minorPh = (minor) ? "${minor}\\." : "[0-9]+\\."
        def exp = /^(.)*?(${major}\.${minorPh}[0-9]+)/
        return exp
    }

    @Override
    def getRcRegexExp() {
        def exp = /^(.)*?(${major}\.${minor}\.[0-9]+-rc\..)/
        return exp
    }

    static Pattern getVersionRegex(){
        return ~/^(.)*?([0-9]+\.[0-9]+\.[0-9]+)/
    }

    static Pattern getRcVersionRegex(){
        return ~/^(.)*?([0-9]+\.[0-9]+\.[0-9]+-rc\..)/
    }

    @Override
    String tobedeterminded(String value) {



    }
}
