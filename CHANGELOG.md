# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]

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
