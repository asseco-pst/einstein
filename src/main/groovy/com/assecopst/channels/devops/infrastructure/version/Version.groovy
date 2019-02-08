package com.asseco.pst.devops.infrastructure.version

import java.util.regex.Matcher

abstract class Version {

    enum Type {
        SEMANTIC,
        LEGACY
    }

    protected String versionStr
    protected List<String> tokenizedVersion

    private static SemanticVersion semanticVersion
    private static LegacyVersion legacyVersion

    static {
        semanticVersion = new SemanticVersion()
        legacyVersion = new LegacyVersion()
    }

    Version() {}

    Version(String aVersionStr) {

        versionStr = aVersionStr

        tokenizeVersion()
        parse()
    }


    static Version factory(String aVersionStr) {

        if(!isValidVersion(aVersionStr))
            throw new Exception("Version '${aVersionStr}' is not a valid version. Accepted version sintaxes: 'x.x.x' or 'x.x.x.x'")

        switch(getVersionType(aVersionStr)) {
            case Type.SEMANTIC:
                return new SemanticVersion(aVersionStr)
                break
            case Type.LEGACY:
                return new LegacyVersion(aVersionStr)
                break
        }

    }

    protected void tokenizeVersion() {
        tokenizedVersion = versionStr.tokenize(".")
    }

    private static Type getVersionType(String aVersionStr) {

        if(semanticVersion.match(aVersionStr))
            return Type.SEMANTIC
        else if(legacyVersion.match(aVersionStr))
            return Type.LEGACY
        else
            throw new Exception("Unable to determine versioning Type for version: '${aVersionStr}'")

    }

    private static boolean isValidVersion(String aVersionStr) {
        return (semanticVersion.match(aVersionStr) || legacyVersion.match(aVersionStr))
    }

    protected int formatNumber(String aNbrStr) {

        int nbr

        try {
            nbr = aNbrStr.trim().toInteger()
        } catch (NumberFormatException ex) {
            throw new Exception("Unable to parse ${aNbrStr} to number. Cause: ${ex}")
        }

        return nbr
    }

    protected boolean isRcTag() {
        return ((Matcher) (versionStr =~ /^.*rc.*/)).matches()
    }


    String getVersionGitRegexExp() {

        String exp

        if(isRcTag())
            exp = getGitMatchRcVersion()
        else
            exp = getGitMatchVersionExp()

        return exp
    }

    boolean matchesVersion(String aVersion) {

        if(isRcTag())
            return (aVersion =~ getRcRegexExp()).matches()
        else
            return (aVersion =~ getVersionRegexExp()).matches()
    }

    protected abstract void parse()

    abstract boolean match(String aVersion)

    abstract String getGitMatchVersionExp()
    abstract String getGitMatchRcVersion()

    abstract def getVersionRegexExp()
    abstract def getRcRegexExp()

}
