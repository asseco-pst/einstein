package io.github.asseco.pst.infrastructure.utils

import io.github.asseco.pst.infrastructure.interfaces.Version
import org.semver4j.Semver
import io.github.asseco.pst.http.RepoExplorerFactory
import io.github.asseco.pst.infrastructure.exceptions.VersionException
import org.gitlab4j.api.models.Tag

import java.util.function.Predicate
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors


class SemanticVersion extends Semver implements Version {
    static final String SNAPSHOT_SUFFIX = "-SNAPSHOT"

    SemanticVersion(String aVersion) {
        super(aVersion)
    }

    /**
     * Creates a Semver version with Type = NPM
     *
     * @param aVersion
     * @return a SemanticVersion (extends Semver)
     */
    static SemanticVersion create(String aVersion) {
        SemanticVersion version

        try {
            version = new SemanticVersion(aVersion.trim())
        } catch (RuntimeException aException) {
            throw new VersionException("Unable to instantiate Semver version '${aVersion}'", aException)
        }
        return version
    }


    /**
     * Checks if the provided version is a SNAPSHOT version
     *
     * @param aVersion
     * @return true if the provided @param represents a SNAPSHOT version
     */
    static boolean isSnapshot(String aVersion) {
        return hasSnapshotSuffix(aVersion)
    }

    /**
     * Checks if the provided version is not a range
     *
     * @param aVersion
     * @return true if is not a range
     */
    static boolean isDeclaredVersion(String aVersion) {
        return !Pattern.compile("[\\^x*~<>=-]").matcher(aVersion).find()
    }

    static String getBiggestVersion(Map<SemanticVersion, String> aVersions) {
        return aVersions.entrySet().stream()
                .max { v1, v2 ->
                    v1.key <=> v2.key
                }
                .map { it.key }
    }

    /**
     * Checks if given list of versions contains non compatible versions among them.
     *
     * @param aVersions
     * @return true if non compatible versions are found
     */
    static boolean hasNonCompatibleVersions(Map<SemanticVersion, String> aVersions) {
        SemanticVersion latestVer
        boolean foundNoncompatibleVersions = false

        aVersions.each {
            SemanticVersion version = it.getKey()

            if (latestVer) {
                if (version.diff(latestVer) == VersionDiff.MAJOR)
                    foundNoncompatibleVersions = true
            }
            latestVer = version
        }

        return foundNoncompatibleVersions
    }

    /**
     * Gets the biggest/most recent version from provided git tags list
     *
     * Sanitizing (@see #sanitizeVersion(String aStrTag)) is important for versions comparison reasons.
     * According to <a href="https://semver.org/#spec-item-11">semver:spec#11.4</a> 1.0.0-rc10 is lower than 1.0.0-rc9
     * (not desired scenario) but 1.0.0-rc.10 is bigger than 1.0.0-rc.9 (desired scenario)
     *
     * @param aVersions - a list of git tags
     * @return a Semver version
     */
    static Semver getBiggestSanitizedVersion(List<String> aVersions) {

        Map<String, Semver> sanitizedTags = new HashMap<String, Semver>()

        aVersions.each { String tag ->
            sanitizedTags.put(sanitizeVersion(tag), create(tag))
        }

        Semver satisfies = sanitizedTags.keySet().collect { create(it.toString()) }.max { a, b ->
            (a <=> b)
        }

        return sanitizedTags[satisfies.getVersion()]
    }

    /**
     * Checks if the current object represents a SNAPSHOT version
     * Similar to @see SemanticVersion#isSnapshot(String aVersion)
     *
     * @return true if the current object is a SNAPSHOT version
     */
    boolean isSnapshot() {
        return hasSnapshotSuffix(this.toString())
    }

    /**
     * Finds a tag in the required project that satisfies the requirement versionRange.
     *
     * It does so by executing the following steps:
     *      * gets all tags from the project that satisfy the requirement versionRange
     *      * returns the highest version that satisfies the versionRange
     *
     * @param aRequirement
     * @return the version value
     */
    static String findSatisfyingVersion(String aNamespace, String aProjectName, String aVersionRange) {
        if (isSnapshot(aVersionRange)) {
            return aVersionRange
        }

        Predicate<? super Tag> predicate = {
            Tag tag ->
                SemanticVersion version = create(tag.getName())
                version.satisfies(aVersionRange)
        }

        if (isDeclaredVersion(aVersionRange)) {
            predicate = {
                Tag tag ->
                    SemanticVersion version = create(tag.getName())
                    version.isEquivalentTo(aVersionRange)
            }
        }

        List<String> tags = RepoExplorerFactory.get()
                .listTags(
                        aNamespace,
                        aProjectName,
                        predicate
                )

        if (!tags)
            throw new VersionException("Unable to get satisfying version for declared dependency: ${aNamespace}/${aProjectName}: ${aVersionRange}")

        Semver biggestVersion = getBiggestSanitizedVersion(filterCustomTags(aVersionRange, tags))
        return biggestVersion.getVersion()
    }

    private static List<String> filterCustomTags(String aVersionRange, List<String> tags) {
        if (aVersionRange.contains("-")) {
            List<String> filteredTags = tags.stream()
                    .filter(t -> t.contains("-") && t.contains(aVersionRange.substring(aVersionRange.indexOf("-"))))
                    .collect(Collectors.toList())

            tags = filteredTags
        }
        tags
    }

    /**
     * Checks if a given string version respects the Semver syntax
     *
     * @param aVersion
     * @return false if it's not a valid Semantic version
     */
    static boolean isValid(String aVersion) {
        try {
            create(aVersion)
            return true
        } catch (ignored) {
            return false
        }
    }

    /**
     * Checks if provided version is greater, equal or lower than the current version <p>
     *
     * Semver specification does not deal with SNAPSHOT versions precedence as desired.<br>
     *
     * According to the lexical sorting of SemVer versions (with letter or hyphens), SNAPSHOT versions are considered
     * greater than alpha, beta or rc versions (i.e, <i>1.0.0-alpha < 1.0.0-beta < 1.0.0.rc < 1.0.0-SNAPSHOT < 1.0.0)</i>.
     *
     * However, since they represent the latest commit on the development branch, SNAPSHOT versions must be considered
     * the lowest version when compared with the above pre-release versions.<br>
     *
     * Therefore, this class overrides de Semver#compareTo() method in order to respect the
     * desired precedence: <i>1.0.0-SNAPSHOT < 1.0.0-alpha < 1.0.0-beta < 1.0.0-rc < 1.0.0<p></i>
     *
     * @see <a href="https://semver.org/#spec-item-11"       >       Semver precedences</a>
     * @see <a href="https://tinyurl.com/y59lvzt8"       >       com.vdurmont.semver4j.Semver#compareTo()</a>
     *
     * @param Semver aVersion
     * @return
     *   - 1 if current version is greater than provided version
     *   - 0 if both versions are equal
     *   - -1 if current version is lower than provided version
     *
     */
    @Override
    int compareTo(Semver aVersion) {
        SemanticVersion comparingVersion = new SemanticVersion(aVersion.getVersion())

        if (this.isSnapshot() || comparingVersion.isSnapshot()) {
            return compareWithSnapshot(comparingVersion)
        }
        return super.compareTo(aVersion)
    }

    private int compareWithSnapshot(SemanticVersion aVersion) {
        int result = comparePrefixTo(aVersion)

        if (result == 0) {
            if (this.isSnapshot()) {
                return -1
            }
            return 1
        }
        return result
    }

    /**
     * Gets the Prefix of the provided version<p>
     *
     * A version Prefix is composed by: Major, Minor and Patch <br>
     * (i.e, the Prefix of version 1.2.3-alpha.1+sha21fds32432 is 1.2.3) <p>
     *
     * @param aVersion
     * @return the version's prefix
     */
    private static String getPrefix(SemanticVersion aVersion) {
        return "${aVersion.getMajor()}.${aVersion.getMinor()}.${aVersion.getPatch()}"
    }

    private static boolean hasSnapshotSuffix(String aVersion) {
        return aVersion.trim().toUpperCase().contains(SNAPSHOT_SUFFIX)
    }

    private int comparePrefixTo(SemanticVersion aVersion) {
        SemanticVersion v1 = create(getPrefix(this))
        SemanticVersion v2 = create(getPrefix(aVersion))

        return (v1 <=> v2)
    }

    /**
     * It "fixes" pre-release versions that missed the dot character between the pre-release type (alpha, beta, rc, ...)
     * and the pre-release index (1, 2, 3, ...)
     *
     * @param aStrTag
     * @return "sanitized" version (i.e 1.0.0-rc1 => 1.0.0-rc.1)
     */
    private static String sanitizeVersion(String aStrTag) {

        String version = aStrTag

        Matcher matcher = Pattern.compile(/^([0-9]+\.[0-9]\.[0-9])(-([aA-zZ]+)([0-9]+.*))/).matcher(version)
        if (matcher.matches()) {
            version = matcher.group(1) + "-" + matcher.group(3) + "." + matcher.group(4)
        }

        return version
    }

}
