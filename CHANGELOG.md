# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]


[1.0.3] - 2020-02-23
### Fixed
- Fixed issue when running `curl` command within a linux SO

[1.0.2] - 2020-01-02
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
