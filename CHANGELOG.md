# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]

[1.5.1] - 2024-10-01
### Fixed
- process to retrieve the latest commits from the default branch. The process was previously considering the 'main' branch for snapshots, but it should have been using the 'next' branch instead.

### Security
- Updated **org.codehaus.groovy:groovy** from `3.0.5` to `3.0.7` to address several vulnerabilities, including deserialization issues that could lead to remote code execution.
  - CVE: [CVE-2020-17521](https://nvd.nist.gov/vuln/detail/CVE-2020-17521)
  - Severity: High

- Updated **org.gitlab4j:gitlab4j-api** from `4.15.4` to `6.0.0-rc.5` to fix various security vulnerabilities and improve overall security posture.
  - The update addresses potential issues around API token exposure and improper input validation.
  - CVE: N/A (Security-related update without a public CVE reference)
  - Severity: Medium

- Updated **org.yaml:snakeyaml** from `1.27` to `2.0` to fix a critical vulnerability that allowed the parsing of malicious YAML documents, leading to remote code execution.
  - CVE: [CVE-2022-25857](https://nvd.nist.gov/vuln/detail/CVE-2022-25857)
  - Severity: Critical

- Replaced **org.slf4j:slf4j-log4j12** `1.7.30` with **org.slf4j:slf4j-reload4j** `2.0.16`, as the older version had security risks related to log4j vulnerabilities. The new `slf4j-reload4j` mitigates these risks by improving log handling.
  - CVE: [CVE-2019-17571](https://nvd.nist.gov/vuln/detail/CVE-2019-17571)
  - Severity: High

- Updated **junit:junit** from `4.12` to `4.13.1` to patch security vulnerabilities related to improper handling of test data, which could lead to potential code execution or resource exhaustion.
  - CVE: N/A
  - Severity: Low
  
[1.5.0] - 2024-06-19
### Fixed
- process to get the latest commit from a default branch, only considering develop branch but some projects is using main branch

[1.4.0] - 2024-06-03
### Added
- Added new semver library to support feature tags (PTEFMBL1191828I-4655)

[1.3.3] - 2023-02-22
### Fixed
- Fixed exportation on custom tags

[1.3.2] - 2022-01-12
### Fixed
- Fixed method that compares which version is bigger, the comparison now is done by the same method

### Changed
- Changed log4j version to a version that has the security fix that resolves the remote code execution issue

[1.3.1] - 2021-08-31
### Fixed
- Fix project's fullname parsing in `calculate command` context so einstein can recognize projects placed within Gitlab
subgroups.

[1.3.0] - 2021-06-28
### Changed
- Refactored logger logic. Split logic between infrastructure and cli code. Default LoggerFactory in use is the one provided
by SL4J. Custom LoggerFactory now only in use in the commands package. This avoids potential conflicts when using Einstein as a library

[1.2.2] - 2021-06-25
### Fixed
- Fixed missing classifier that was causing the slim library to be overwritten by the fat one (needed to lose some additional weight) 

[1.2.1] - 2021-06-25
### Changed
- Changed the publication plugin in order to avoid sending a fat jar to maven and, in this case, send a standard java library

[1.2.0] - 2021-06-23
### Changed
- Changed SemanticVersion class to fetch the equivalent tag version from Gitlab for a declared version.

[1.1.0] - 2021-06-08
### Changed
- Update `README` file
- Updated einstein thread handling process. It now uses a custom ThreadPoolExecutor with an initial pool size of 35 threads

[1.0.5] - 2021-04-29
### Fixed
- Commented out dependency exclusion for log4j in shadow jar build. This was preventing logging to console.

[1.0.4] - 2021-04-20
### Fixed
- Every time a calculation os performed, clean collection of threads' uncaught exceptions, eventually thrown on previous
  calculations.
- Applied some missing `syncronized` blocks and removed some others unrequired
### Changed
- Increased timeout duration from 300 s to 600 seconds

[1.0.3] - 2021-02-23
### Fixed
- Fixed issue when running `curl` command within a linux SO

[1.0.2] - 2021-01-02
### Fixed
- On Gitlab Api `curl` requests, accept insecure connections

[1.0.1] - 2020-12-28
### Fixed
- `shadow jar` does now excludes `logging` related artifacts dependencies.

[1.0.0] - 2020-11-25
### Added
- Support to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
- Support for [Semver version ranges](https://devhints.io/semver)
- Get projects information from a Gitlab Repository
- Added `einstein.yaml` file
- Check compatibility between identified versions of a specific project
- Save identified dependencies to an external file
- Add a timeout step that suspends the process if it's taking too long
- Log4j integration
