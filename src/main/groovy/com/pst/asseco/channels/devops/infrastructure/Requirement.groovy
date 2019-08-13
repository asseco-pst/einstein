package com.pst.asseco.channels.devops.infrastructure
/**
 * This <strong>class represents a single entry</strong> on the einstein.yaml file.
 * ie.:<br>
 *
 * <code>
 * [projectNamespace]:          # i.e, middleware <br>
 *     - [projectName]:  [range]   # i.e, project-a: ^1.0              <---- this is an entry <br>
 *     - [projectName]:  [range]   # i.e: project-b: 1.0.0-SNAPSHOT    <---- this is another entry<br>
 * </code>
 *<br>
 *  Ranges are defined according Semver 2.0.0 specification
 *  @see <a href="https://devhints.io/semver">Semver accepted Ranges</a>
 *
 */
class Requirement {

    String projectNamespace
    String projectName
    String versionRange

    @Override
    String toString() {
        return "$projectNamespace/$projectName:$versionRange"
    }
}
