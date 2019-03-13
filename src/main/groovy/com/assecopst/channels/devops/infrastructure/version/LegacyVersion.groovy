package com.assecopst.channels.devops.infrastructure.version

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
    String getVersionMatchPattern() {

        String pattern = /${getVersionPrefixPattern()}(([0-9]+\.[0-9]+\.[0-9]+\.[0-9]+)(${getRcPostfixPattern()})?)$/
        return pattern
    }

    @Override
    boolean match(String aVersion) {

        return (aVersion =~ getVersionMatchPattern()).matches()
    }

    @Override
    String getVersionRegexPattern() {

        String nydPlaceHolder = (nyd) ? "$nyd" : "[0-9]+"
        String majorPlaceHolder = (major) ? "$major" : "[0-9]+"
        String minorPlaceHolder = (minor) ? "$minor" : "[0-9]+"

        String patternExp =
                /${getVersionPrefixPattern()}(${nydPlaceHolder}\.${majorPlaceHolder}\.${minorPlaceHolder}\.[0-9]+)/

        return patternExp
    }

    @Override
    String getRcVersionRegexPattern() {

        String nydPlaceHolder = (nyd) ? "$nyd" : "[0-9]+"
        String majorPlaceHolder = (major) ? "$major" : "[0-9]+"
        String minorPlaceHolder = (minor) ? "$minor" : "[0-9]+"

        String patternExp =
                /${getVersionPrefixPattern()}(${nydPlaceHolder}\.${majorPlaceHolder}\.${minorPlaceHolder}[0-9]+
                    ${getRcPostfixPattern()})/

        return patternExp
    }
}
