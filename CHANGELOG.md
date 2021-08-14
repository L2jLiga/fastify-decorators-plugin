# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- IDEA 2021.2 support

### Fixed
- get rid of deprecated methods usage

## [0.11.0]

### Added

- IDE 2021.1 support

### Miscellaneous

- Exclude Kotlin stdlib from distribution
- Update dependencies
- Setup security policy
- Integrate SARIF report with GitHub security
- Correct links in README
- Switched to new IR backend


## [0.10.0] - 2021-01-16

### Added

- Quick-fix: Remove redundant initializer dependencies
- Quick-fix: Take format settings into account

### Fixed

- NPE in several inspections

### Miscellaneous

- Dependencies: update Gradle to 6.8.0 (was 6.7.0)
- Dependencies: update Kotlin to 1.4.21 (was 1.4.10)
- Dependencies: update org.jetbrains.intellij to 0.6.5 (was 0.6.2)


## [0.0.9] - 2020-11-07
### Added
- Core: Compatibility with IDEA 2020.3 (EAP)
- Inspections: Detect redundant initializer dependencies

### Miscellaneous
- Core: updated plugin icon
- Dependencies: update Gradle to 6.7.0 (was 6.6.1)
- Dependencies: update Kotlin to 1.4.10 (was 1.4.0)
- Dependencies: update org.jetbrains.intellij to 0.6.2 (was 0.4.21)


## [0.0.8] - 2020-09-03
### Added
- Intention(refactoring): Actions to easy swap between DI usage (@Inject, getInstanceByToken or constructor)

### Miscellaneous
- Dependencies: update Gradle to 6.6.1 (was 6.5.1)
- Dependencies: update Kotlin to 1.4.0 (was 1.3.72)


## [0.0.7] - 2020-07-10
### Added
- Inspection: Warn when @Inject decorator used for static fields
- Quick-fix: Replace "@Inject" with "getInstanceByToken"

### Fixed
- type cast exception when @Inject argument is literal (string, number etc)
- false-positive error for FastifyInstanceToken usage in @Inject
- controller was marked as unused when apply "add default export" quick-fix

### Miscellaneous
- Dependencies: update Gradle to 6.5.1 (was 6.5)


## [0.0.6] - 2020-06-20
### Added
- Core: support IDEA 2020.2 (EAP)
- Inspection: highlight errors in @Inject decorator usage
- Inspection: disabled controllers default export inspection by default

### Miscellaneous
- Dependencies: update org.jetbrains.intellij to 0.4.21 (was 0.4.18)
- Dependencies: update Graddle to 6.5 (was 6.3)


## [0.0.5] - 2020-04-15
### Added
- Quick-fix: enable emitting decorator metadata when it disabled.
- Inspection: highlight constructor if emitting decorator metadata disabled

### Miscellaneous
- Upgrade gradle to 6.3 (was 5.1.1)
- Dependencies: update org.jetbrains.intellij to 0.4.18 (was 0.4.16)
- Dependencies: update kotlin to 1.3.72 (was 1.3.70)
- Dependencies: remove junit
- Plugins: remove java


## [0.0.4] - 2020-02-22
### Added
- Inspection: Dependency Injection can not work without emitting decorators metadata.

### Fixed
- Inspection: Controller arguments inspection shows error even class is not annotated.

### Miscellaneous
- Dependencies: update org.jetbrains.intellij to 0.4.16 (was 0.4.15)
- Dependencies: update kotlin to 1.3.70 (was 1.3.61)


## [0.0.3] - 2020-03-19
### Added
- static description for all inspections
- support for 2020.1 EAP

### Fixed:

- ensure import statement in valid place when applying "Annotate "Class" with @Service decorator"
- controller argument inspection worked wrong when injectable service has default export

## [0.0.2] - 2020-02-16

Initial release of plugin. Includes several inspections with quick-fixes and implicit usage providers.


[Unreleased]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/v0.10.0...HEAD

[0.10.0]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/v0.9...v0.10.0

[0.0.9]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.8...v0.9

[0.0.8]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.7...0.8

[0.0.7]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.6...0.7

[0.0.6]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.5...0.6

[0.0.5]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.4...0.5

[0.0.4]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.3...0.4

[0.0.3]: https://github.com/L2jLiga/fastify-decorators-plugin/compare/0.2...0.3

[0.0.2]: https://github.com/L2jLiga/fastify-decorators-plugin/releases/tag/0.2