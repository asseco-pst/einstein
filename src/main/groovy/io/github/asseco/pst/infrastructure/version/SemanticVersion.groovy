package io.github.asseco.pst.infrastructure.version

class SemanticVersion extends Version {


    protected SemanticVersion() {}

    SemanticVersion(String aVersionStr) {
        super(aVersionStr)
    }


    @Override
    int compareTo(Version aVersion) {

        if (!(aVersion instanceof SemanticVersion))
            throw new IllegalArgumentException("Unable to compare versions from multiple specifications...")

        SemanticVersion version = (SemanticVersion) aVersion

        if (major > version.major)
            return 1
        if (version.major > major)
            return -1
        // at this point, major fields from both versions are equal. Lets check the minor fields
        if (minor > version.minor)
            return 1
        if (version.minor > minor)
            return -1
        // at this point, minor fields from both versions are equal. Lets check the patch fields
        if (patch > version.patch)
            return 1
        if (version.patch > patch)
            return -1
        // at this point, major, minor and patch fields from both versions are all equal.
        // Both versions are equal
        return 0
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
