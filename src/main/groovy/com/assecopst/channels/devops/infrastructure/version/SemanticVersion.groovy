package com.assecopst.channels.devops.infrastructure.version

class SemanticVersion extends Version {


    protected SemanticVersion() {}

    SemanticVersion(String aVersionStr) {
        super(aVersionStr)
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
    String getVersionMatchPattern() {

        String pattern = /${getVersionPrefixPattern()}(([0-9]+\.[0-9]+\.[0-9]+)(${getRcPostfixPattern()})?)$/
        return pattern
    }

    @Override
    boolean match(String aVersion) {

        return (aVersion =~ getVersionMatchPattern()).matches()
    }

    @Override
    String getVersionRegexPattern() {

        String majorPlaceHolder = (major) ? "${major}" : "[0-9]+"
        String minorPlaceHolder = (minor) ? "${minor}" : "[0-9]+"

        String patternExp = /${getVersionPrefixPattern()}($majorPlaceHolder\.$minorPlaceHolder\.[0-9]+)/

        return patternExp
    }

    @Override
    String getRcVersionRegexPattern() {

        String majorPlaceHolder = (major) ? "${major}" : "[0-9]+"
        String minorPlaceHolder = (minor) ? "${minor}" : "[0-9]+"

        String patternExp =
                /${getVersionPrefixPattern()}($majorPlaceHolder\.$minorPlaceHolder\.[0-9]+${getRcPostfixPattern()})/

        return patternExp
    }
}
