package com.assecopst.channels.devops.infrastructure.version

import com.assecopst.channels.devops.infrastructure.utils.Console

import java.util.regex.Matcher

abstract class Version implements Comparable<Version> {

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

    @Override
    String toString() {
        return versionStr
    }

    protected String purge() {

        Matcher matcher = versionStr =~ getVersionMatchPattern()

        try {
            if (matcher.matches()) {
                return matcher.group(3)
            }
            throw new Exception("'$versionStr' doesn't match pattern '${getVersionMatchPattern()}'")
        } catch (Exception e) {
            Console.err("Unable to purge version '$versionStr'. Cause: $e")
            throw e
        }
    }

    static synchronized String extractVersionFrom(String aSentence) {

        Version version

        switch (getVersionType(aSentence)) {
            case Type.SEMANTIC:
                version = semanticVersion
                break
            case Type.LEGACY:
                version = legacyVersion
                break
            default:
                throw new IllegalArgumentException("Provided version string does not match any of the " +
                        "existing versions specifications...")
        }

        return extractVersion(version, aSentence)
    }

    private static String extractVersion(Version aVersion, String aSentence) {

        Matcher matcher

        if ((aSentence =~ aVersion.getRcPostfixPattern()).matches()) {
            matcher = (aSentence =~ aVersion.getRcVersionRegexPattern())
        } else if ((aSentence =~ aVersion.getVersionRegexPattern()).matches()) {
            matcher = (aSentence =~ aVersion.getVersionRegexPattern())
        } else {
            throw new IllegalArgumentException("Unable to extract ${aVersion.getClass().getSimpleName()} version from String '$aSentence'")
        }

        String extractedVersion

        try {
            if (matcher)
                extractedVersion = matcher.group(2)
            else
                throw new Exception("Unable to extract version. 'matcher' is undefined...")
        } catch (Exception e) {
            Console.err("Unable to get version from matcher's group. Cause $e")
            throw e
        }

        return extractedVersion
    }

    synchronized static List<Version> factory(def aVersions) {

        if (!aVersions)
            return []

        List<Version> versions = []
        aVersions.each { versionStr ->
            versions << factory(versionStr)
        }

        return versions
    }

    synchronized static Version factory(String aVersionStr) {

        if (!isValidVersion(aVersionStr))
            throw new Exception("Version '${aVersionStr}' is not a valid version. Accepted version sintaxes: 'x.x.x(-rc\\.?([0-9]+)?)?' or 'x.x.x.x(-rc\\.?([0-9]+)?)?'")

        switch (getVersionType(aVersionStr)) {
            case Type.SEMANTIC:
                return new SemanticVersion(aVersionStr)
                break
            case Type.LEGACY:
                return new LegacyVersion(aVersionStr)
                break
        }

    }

    synchronized static boolean hasMultipleVersionSpecifications(Set<Version> aVersions) {

        boolean hasMultSpecs = false

        Version lastStatedVersion
        aVersions.each { version ->
            if (lastStatedVersion && (lastStatedVersion.getClass() != version.getClass())) {
                Console.warn("Versions ${lastStatedVersion.getVersionStr()} and ${version.getVersionStr()} have " +
                        "different specification (${lastStatedVersion.getClass().getSimpleName()} <> ${version.getClass().getSimpleName()})")
                hasMultSpecs = true
            }
            lastStatedVersion = version
        }

        return hasMultSpecs
    }

    synchronized static boolean hasNonBackwardCompatibleVersions(Set<Version> aVersions) {
        return (hasMajorBreak(aVersions) || containsRCVersionAndStableVersion(aVersions))
    }

    /**
     *
     * @param aVersions - this parameter represents a Collection. It can be a List<String> or a Set<String>
     * @return the biggest version on the given Collection
     */
    synchronized static <T extends Collection> String getBiggestVersion(T aVersions) {

//        // cast parameter so one can apply the reverse() method over it
//        List<String> versions = (aVersions instanceof List) ? aVersions.clone() : new ArrayList<String>(aVersions as Collection)
//
//        // convert versions - from the given 'aVersions' - to integers and get/calculate the biggest version on the list
//        int biggestIntegerVersion = versions.stream().collect({ version -> version.tokenize(".").join("").toInteger() }).sort().reverse()[0]

        // now that we have the 'biggestIntegerVersion', get the "string version" of it

        List<Version> versionsToCompare = []
        aVersions.each { v ->
            try {
                Version version = (Version) v
                versionsToCompare << version
            } catch (Exception e) {
                Console.err("Error calculating the biggest version. Parameters accepted: Collection<? extends Version>")
                throw new IllegalArgumentException(e)
            }
        }

        Collections.sort(versionsToCompare)

        return versionsToCompare.last()
    }

    protected String getVersionPrefixPattern() {
        return "^([a-zA-Z])*?"
    }

    protected String getRcPostfixPattern() {

        return "-rc\\.?([0-9]+)?"
    }

    private static boolean hasMajorBreak(Set<Version> aVersions) {

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

    private static boolean containsRCVersionAndStableVersion(Set<Version> aVersions) {

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
        tokenizedVersion = purge().tokenize(".")
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

    synchronized boolean isRcTag() {
        return ((Matcher) (versionStr =~ /^(.)*?${getRcPostfixPattern()}$/)).matches()
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
        return (versionStr =~ getVersionRegexPattern()).matches()
    }

    synchronized boolean matchesVersion(String aVersion) {

        if (isRcTag())
            return (aVersion =~ getRcVersionRegexPattern()).matches()
        else
            return (aVersion =~ getVersionRegexPattern()).matches()
    }


    protected abstract void parse()

    protected abstract boolean checkIfHasMajorBreak(Version aVer1, Version aVer2)

    abstract String getVersionMatchPattern()

    abstract boolean match(String aVersion)

    abstract String getVersionRegexPattern()

    abstract String getRcVersionRegexPattern()
}
