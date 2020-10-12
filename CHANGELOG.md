# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Added
- Support for Semver version ranges (see [this](https://devhints.io/semver))
- Calculate dependencies between projects of a same Product
- Get projects information from a Gitlab Repository
- Check compatibility between identified versions of a specific project
- Support Semantic Versioning (i.e: major.minor.patch)
- Support Legacy Versioning (i.e: nyd.major.minor.patch)
- Support RC tags (i.e: v1.2.3-rc\.?([0-9]+)?)
- Save identified dependencies to an external file
- Add a timeout step that suspends the process if ti takes more than 120 seconds processing dependencies
- Add log4j implementation for logging