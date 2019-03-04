package com.assecopst.channels.devops.infrastructure.version

import com.assecopst.channels.devops.infrastructure.utils.Console

import java.util.regex.Matcher

abstract class Version {

    int major, minor, patch

    enum Type {
        SEMANTIC,
        LEGACY
    }

    String versionStr
    protected List<String> tokenizedVersion

    private static SemanticVersion semanticVersion
    private static LegacyVersion legacyVersion

    static {
        semanticVersion = new SemanticVersion()
        legacyVersion = new LegacyVersion()
    }

    Version() {}

    protected Version(String aVersionStr) {

        versionStr = aVersionStr

        tokenizeVersion()
        parse()
    }


    static List<Version> factory(def aVersions) {

        if (!aVersions)
            return []

        List<Version> versions = []
        aVersions.each { versionStr ->
            versions << factory(versionStr)
        }

        return versions
    }

    static Version factory(String aVersionStr) {

        if (!isValidVersion(aVersionStr))
            throw new Exception("Version '${aVersionStr}' is not a valid version. Accepted version sintaxes: 'x.x.x' or 'x.x.x.x'")

        switch (getVersionType(aVersionStr)) {
            case Type.SEMANTIC:
                return new SemanticVersion(aVersionStr)
                break
            case Type.LEGACY:
                return new LegacyVersion(aVersionStr)
                break
        }

    }

    static boolean hasMultipleVersionSpecifications(List<Version> aVersions) {

        boolean hasMultSpecs = false

        Version lastStatedVersion
        aVersions.each { version ->
            if (lastStatedVersion && (lastStatedVersion.getClass() != version.getClass())) {
                Console.warn("Versions ${lastStatedVersion.getVersionStr()} and ${version.getVersionStr()} have " +
                        "different specification (${lastStatedVersion.getClass().getName()} <> ${version.getClass().getName()})")
                hasMultSpecs = true
            }
            lastStatedVersion = version
        }

        return hasMultSpecs
    }

    static boolean hasNonBackwardCompatibleVersions(List<Version> aVersions) {
        return (hasMajorBreak(aVersions) || containsRCVersionAndStableVersion(aVersions))
    }

    /**
     *
     * @param aVersions - this parameter represents a Collectio. It can be a List<String> or a Set<String>
     * @return the biggest version on the given Collection
     */
    static String getBiggestVersion(def aVersions) {

        // cast parameter so one can apply the reverse() method over it
        List<String> versions = (aVersions instanceof List) ? aVersions.clone() : new ArrayList<String>(aVersions as Collection)

        // convert versions - from the given 'aVersions' - to integers and get/calculate the biggest version on the list
        int biggestIntegerVersion = versions.stream().collect({ version -> version.tokenize(".").join("").toInteger() }).sort().reverse()[0]

        // now that we have the 'biggestIntegerVersion', get the "string version" of it
        return versions.stream().filter({ version -> version.tokenize(".").join("").toInteger() == biggestIntegerVersion }).collect()[0]
    }

    private static boolean hasMajorBreak(List<Version> aVersions) {

        boolean hasMajorBreak = false
        Version lastStatedVersion
        aVersions.each { version ->
            if (!lastStatedVersion) {
                lastStatedVersion = version
                return
            }
            hasMajorBreak = version.isMajorBreak(version, lastStatedVersion)

            lastStatedVersion = version
        }

        return hasMajorBreak
    }

    private static boolean containsRCVersionAndStableVersion(List<Version> aVersions) {

        boolean hasStableVersions = false
        boolean hasRCVersions = false

        aVersions.each { version ->
            if (version.isRcTag()) {
                hasRCVersions = true
                return
            }
            hasStableVersions = version.isStableVersion()
        }

        if (hasStableVersions && hasRCVersions)
            Console.warn("Found RC versions and Stable versions as dependencies for the same Project -> " +
                    "${aVersions.stream().collect({ version -> version.getVersionStr() }).join(" <> ")}")

        return (hasStableVersions && hasRCVersions)
    }

    protected void tokenizeVersion() {
        tokenizedVersion = versionStr.tokenize(".")
    }

    private static Type getVersionType(String aVersionStr) {

        if (semanticVersion.match(aVersionStr))
            return Type.SEMANTIC
        else if (legacyVersion.match(aVersionStr))
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

    protected boolean isMajorBreak(Version aVer1, Version aVer2) {
        if (aVer1.getClass() != aVer2.getClass())
            throw new Exception("Unable to check compatibility of versions with origin from different specifications!" +
                    "'${aVer1.getVersionStr()}' is a ${aVer1.getClass().getName()} version and" +
                    "'${aVer2.getVersionStr()}' is a ${aVer2.getClass().getName()} version")

        checkIfHasMajorBreak(aVer1, aVer2)
    }

    protected boolean breakOnMajorFields(Version aVer1, Version aVer2) {

        List<Integer> majors = []
        majors << aVer1.getMajor()
        majors << aVer2.getMajor()

        majors = majors.reverse()

        return ((majors[0] - majors[1]) >= 1)
    }

    protected isStableVersion() {
        return (versionStr =~ getVersionRegexExp()).matches()
    }

    String getVersionGitRegexExp() {

        String exp

        if (isRcTag())
            exp = getGitMatchRcVersion()
        else
            exp = getGitMatchVersionExp()

        return exp
    }

    boolean matchesVersion(String aVersion) {

        if (isRcTag())
            return (aVersion =~ getRcRegexExp()).matches()
        else
            return (aVersion =~ getVersionRegexExp()).matches()
    }

    String getTagFromExp(String aExp) {

        Matcher tagMatcher = (aExp =~ getVersionRegexExp())

        if (tagMatcher)
            return tagMatcher.group(2)
        else
            throw new Exception("Unable to get Tag from expression: '${aExp}'.")
    }


    protected abstract void parse()

    protected abstract boolean checkIfHasMajorBreak(Version aVer1, Version aVer2)

    abstract boolean match(String aVersion)

    abstract String getGitMatchVersionExp()

    abstract String getGitMatchRcVersion()

    abstract def getVersionRegexExp()

    abstract def getRcRegexExp()
}
