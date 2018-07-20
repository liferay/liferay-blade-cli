# Liferay Blade CLI Change Log

## 3.1.1 - 2018-07-18 - 5d9faab454e1023e3fbe8502365f752c26fcc7c9

- [BLADE-211]: Add Changelog using changelog plugin
- [BLADE-253]: Specify blade version in build config
- [BLADE-257]: Correct server start command
- [BLADE-258]: Always alert users of deployment errors
- [BLADE-259]: Fix NPE in server stop command
- [BLADE-260]: Fix exception in server stop command
- [BLADE-262]: Update to project template 4.1.1 artifact
- [BLADE-264]: Update default version to 7.1

## 3.1.0 - 2018-07-05 - 570a5b7ae626ee0e7bf8751656104c5e33f8512b

- [BLADE-214]: Create a `blade-extension` blade sample project, demonstrating how blade may be extended with custom commands
- [BLADE-231]: Blade Custom Project Template Support
- [BLADE-244]: As a developer, I would like my tomcat logs to be colorized
- [BLADE-246]: create project from service template doesn't show that -s <service> is required
- [BLADE-250]: extension install fails if you install same extension twice
- [BLADE-251]: `blade gw` sometimes has trouble finding gradlew

Updated to use Project Templates 4.1.0
 - [IDE-4081]: Blade's MVC Portlet template doesn't generate javax.portlet.title Language key properly
 - [LPS-78045]: Remove exported package from portal-portlet-bridge-soy
 - [LPS-79301]: Project Templates: Remove build.gradle if build type is maven, pom.xml if gradle
 - [LPS-79417]: Blade template to create a social bookmark module
 - [LPS-79495]: Store Liferay-Versions in MANIFEST.MF of Project Templates
 - [LPS-79496]: Change project templates velocity templates to check if Liferay Version starts with 7.0, 7.1, rather than checking whole string
 - [LPS-79653]: Portlet 3.0: Upgrade to the Portlet 3.0.0 API
 - [LPS-80284]: Update bndlib to 3.5.0
 - [LPS-80404]: As a developer, I would like to see stacktraces from the gradlerunner build
 - [LPS-80472]: Use local npm proxy in CI
 - [LPS-82590]: Move project templates off snapshots

## 3.0.0 - 2018-03-30 - 52ac672f21e8503f4371b6dbc403a9527582eff4

### Added
- [BLADE-190]: As a developer, I would like blade to support deploying wars to Liferay
- [BLADE-199]: As a developer, I would like to create a liferay maven workspace using blade
- [BLADE-201]: Add log settings to all samples so their INFO logging can be seen
- [BLADE-211]: Create CHANGELOG.md file in github for keeping track of changes
- [BLADE-228]: `blade create` should support specifying the Liferay Version

### Changed
- [BLADE-202]: Update Blade CLI to use JCommander
- [BLADE-207]: Upgrade all Blade Samples to set up logging
- [BLADE-214]: Removed install command `blade install ...` and shell command should be used instead `blade sh install ...`
- [BLADE-226]: Print warning and usage info when `blade create -t fragment` is invoked without -H and -h flags
- [BLADE-230]: `blade create` `-t` argument should be mandatory


### Fixed
- [BLADE-193]: Deploy Command does not properly handle bundle states
- [BLADE-206]: `blade create` puts wars inside the modules folder, they should go in the wars folder
- [BLADE-208]: Restore `blade version` command and add a test for it
- [BLADE-209]: Restore `blade help` command and add a test for it
- [BLADE-210]: blade create inside of 'modules' subfolder of workspace fails to create a 'workspace' type of project
- [BLADE-212]: blade init command failed for no destination
- [BLADE-216]: Generating Blade Samples with Blade CLI is outdated and broken
- [BLADE-219]: `server start` command can result in java.lang.IllegalStateException
- [BLADE-227]: `blade help` is currently too verbose, should just be command names with description
- [BLADE-233]: `blade deploy` causes java.lang.NumberFormatException: For input string: "lb -s -u"

## 2.3.1 - 2017-11-21

### Changed

- [LPS-75805]: CSS Builder 2.0.2

## 2.3.0 - 2017-11-02

### Changed
- Updated Gradle Plugins Workspace
  - [LPS-73746]: Trim authentication token in case users add extra lines into their token file.
  - [LPS-73913]: Add the ability to pass the Liferay bundle authentication token password from a file by setting the liferay.workspace.bundle.token.password.file property in gradle.properties
  - [LPS-74124]: Add the ability to configure the cache directory for downloaded Liferay bundles by setting the liferay.workspace.bundle.cache.dir property in gradle.properties
  - [LPS-74818]: Fail the build if the source and destination of the downloadBundle task are the same
  - [LPS-75479]: Use Liferay 7.0.4 GA5 by default
- [Updated Liferay Gradle Plugin]
  - Updated Service Builder to 1.0.173
- [LPS-74994]: Added NPM Project Templates
- [LPS-75587]: Use latest Project Template Archetypes


[BLADE-190]: https://issues.liferay.com/browse/BLADE-190
[BLADE-193]: https://issues.liferay.com/browse/BLADE-193
[BLADE-199]: https://issues.liferay.com/browse/BLADE-199
[BLADE-201]: https://issues.liferay.com/browse/BLADE-201
[BLADE-202]: https://issues.liferay.com/browse/BLADE-202
[BLADE-206]: https://issues.liferay.com/browse/BLADE-206
[BLADE-207]: https://issues.liferay.com/browse/BLADE-207
[BLADE-208]: https://issues.liferay.com/browse/BLADE-208
[BLADE-209]: https://issues.liferay.com/browse/BLADE-209
[BLADE-210]: https://issues.liferay.com/browse/BLADE-210
[BLADE-211]: https://issues.liferay.com/browse/BLADE-211
[BLADE-211]: https://issues.liferay.com/browse/BLADE-211
[BLADE-212]: https://issues.liferay.com/browse/BLADE-212
[BLADE-214]: https://issues.liferay.com/browse/BLADE-214
[BLADE-214]: https://issues.liferay.com/browse/BLADE-214
[BLADE-216]: https://issues.liferay.com/browse/BLADE-216
[BLADE-218]: https://issues.liferay.com/browse/BLADE-218
[BLADE-219]: https://issues.liferay.com/browse/BLADE-219
[BLADE-226]: https://issues.liferay.com/browse/BLADE-226
[BLADE-227]: https://issues.liferay.com/browse/BLADE-227
[BLADE-228]: https://issues.liferay.com/browse/BLADE-228
[BLADE-230]: https://issues.liferay.com/browse/BLADE-230
[BLADE-231]: https://issues.liferay.com/browse/BLADE-231
[BLADE-233]: https://issues.liferay.com/browse/BLADE-233
[BLADE-244]: https://issues.liferay.com/browse/BLADE-244
[BLADE-246]: https://issues.liferay.com/browse/BLADE-246
[BLADE-250]: https://issues.liferay.com/browse/BLADE-250
[BLADE-251]: https://issues.liferay.com/browse/BLADE-251
[BLADE-253]: https://issues.liferay.com/browse/BLADE-253
[BLADE-257]: https://issues.liferay.com/browse/BLADE-257
[BLADE-258]: https://issues.liferay.com/browse/BLADE-258
[BLADE-259]: https://issues.liferay.com/browse/BLADE-259
[BLADE-260]: https://issues.liferay.com/browse/BLADE-260
[BLADE-262]: https://issues.liferay.com/browse/BLADE-262
[BLADE-264]: https://issues.liferay.com/browse/BLADE-264
[IDE-4081]: https://issues.liferay.com/browse/IDE-4081
[LPS-73746]: https://issues.liferay.com/browse/LPS-73746
[LPS-73913]: https://issues.liferay.com/browse/LPS-73913
[LPS-74124]: https://issues.liferay.com/browse/LPS-74124
[LPS-74818]: https://issues.liferay.com/browse/LPS-74818
[LPS-74994]: https://issues.liferay.com/browse/LPS-74994
[LPS-75479]: https://issues.liferay.com/browse/LPS-75479
[LPS-75587]: https://issues.liferay.com/browse/LPS-75587
[LPS-75805]: https://issues.liferay.com/browse/LPS-75805
[LPS-78045]: https://issues.liferay.com/browse/LPS-78045
[LPS-79301]: https://issues.liferay.com/browse/LPS-79301
[LPS-79417]: https://issues.liferay.com/browse/LPS-79417
[LPS-79495]: https://issues.liferay.com/browse/LPS-79495
[LPS-79496]: https://issues.liferay.com/browse/LPS-79496
[LPS-79653]: https://issues.liferay.com/browse/LPS-79653
[LPS-80284]: https://issues.liferay.com/browse/LPS-80284
[LPS-80404]: https://issues.liferay.com/browse/LPS-80404
[LPS-80472]: https://issues.liferay.com/browse/LPS-80472
[LPS-82590]: https://issues.liferay.com/browse/LPS-82590
[Updated Liferay Gradle Plugin]: https://github.com/liferay/liferay-portal/blob/master/modules/sdk/gradle-plugins/CHANGELOG.markdown#3523---2017-10-26