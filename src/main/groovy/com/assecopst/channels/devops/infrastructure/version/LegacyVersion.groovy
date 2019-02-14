package com.assecopst.channels.devops.infrastructure.version

import java.util.regex.Matcher

class LegacyVersion extends Version {

    int nyd

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
    protected boolean checkIfHasMajorBreak(Version aVer1, Version aVer2) {
        return (breakOnNydFields((LegacyVersion) aVer1, (LegacyVersion) aVer2) || breakOnMajorFields(aVer1, aVer2))
    }

    private boolean breakOnNydFields(LegacyVersion aVer1, LegacyVersion aVer2) {

        List<Integer> nyds = []
        nyds << aVer1.getNyd()
        nyds << aVer2.getNyd()

        nyds = nyds.reverse()

        return ((nyds[0] - nyds[1]) >= 1)
    }

    @Override
    boolean match(String aVersion) {
        return ((Matcher) (aVersion =~ /([0-9]+\.[0-9]+\.[0-9]+\.?[0-9]*)/)).matches()
    }

    @Override
    String getGitMatchVersionExp() {
        String minorPh = (minor) ? "${minor}." : ""
        return "${nyd}.${major}.${minorPh}*"
    }

    @Override
    String getGitMatchRcVersion() {
        return "${nyd}.${major}.${minor}.*rc*"
    }

    @Override
    def getVersionRegexExp() {
        String minorPh = (minor) ? "${minor}\\." : "[0-9]+\\."
        def exp = /^(.)*?(${nyd}\.${major}\.${minorPh}[0-9]+)/
        return exp
    }

    @Override
    def getRcRegexExp() {
        def exp = /^(.)*?(${nyd}\.${major}\.${minor}\.[0-9]+-rc\..)/
        return exp
    }
}
