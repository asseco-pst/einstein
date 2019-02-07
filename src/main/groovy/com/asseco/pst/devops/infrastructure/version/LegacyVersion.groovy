package com.asseco.pst.devops.infrastructure.version

import java.util.regex.Matcher

class LegacyVersion extends Version {

    int nyd, major, minor, patch

    protected LegacyVersion() {}

    LegacyVersion(String aVersionStr) {
        super(aVersionStr)
    }

    @Override
    protected void parse() {

        nyd = formatNumber(tokenizedVersion[0])
        major = formatNumber(tokenizedVersion[1])
        minor = formatNumber(tokenizedVersion[2])
        patch = formatNumber(tokenizedVersion[3])
    }

    @Override
    boolean match(String aVersion) {
        return ((Matcher) (aVersion =~ /([0-9]+\.[0-9]+\.[0-9]+\.?[0-9]*)/)).matches()
    }

    @Override
    String getGitMatchVersionExp() {
        return "${nyd}.${major}.*"
    }

    @Override
    String getGitMatchRcVersion() {
        return "${nyd}.${major}.${minor}.*rc*"
    }

    @Override
    def getVersionRegexExp() {
        def exp = /^(.)*?(${nyd}\.${major}\.[0-9]+\.[0-9])/
        return exp
    }

    @Override
    def getRcRegexExp() {
        def exp = /^(.)*?(${nyd}\.${major}\.${minor}\.[0-9]+-rc\..)/
        return exp
    }
}
