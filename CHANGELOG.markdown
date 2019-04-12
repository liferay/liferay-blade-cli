# Liferay Blade CLI Change Log

## 3.6.0 - 2019-4-12 - 907dbac73496b2436fc44f03e71a19c522dc3c9a

- [BLADE-373]: Change .blade/settings.properties to .blade.properties, add an auto-migration
- [BLADE-392]: `blade server run` should work on Maven
- [BLADE-398]: Create blade server start / run tests for verifying custom port functionality
- [BLADE-407]: Convert command should support "source" to set the plugins sdk location
- [BLADE-409]: support migration of portal jars in liferay-plugin-package.properties
- [BLADE-411]: Blade buildService implementation for Maven
- [BLADE-412]: Restore ability to use old gogo shell deploy functionality in Blade

## 3.5.0 - 2019-2-25 - dae6dffde28c20d3420d6bb34c8bc6e661a68c33

- [BLADE-343]: Pass in environment param when running blade server init
- [BLADE-383]: Exception stack trace is printed when certain commands are executed
- [BLADE-385]: Better Blade Maven Workspace Detection and Handling
- [BLADE-386]: remove unneeded project template test builds
- [BLADE-387]: Blade should run even if custom extensions have errors
- [BLADE-388]: snapshots script is broken, doesn't embed correct version of maven profile
- [BLADE-389]: blade prompter can hang in certain circumstances
- [BLADE-390]: Turn parallel tests off by default. turn back on by passing in param
- [BLADE-394]: Add unit tests for Blade Server -d, --debug flag behavior
- [BLADE-395]: Add a -p, --port flag for blade server start and blade server run
- [BLADE-397]: blade server start -t does not tail the log for wildfly

## 3.4.3 - 2019-1-23 - 53683e6dee0a07972b79493944328e56bfaab9cd

- [BLADE-377]: init command should honor the -p, --profile-name flag over any defaults
- [BLADE-380]: Separate handling of settings directory and extensions directory
- [BLADE-381]: 'blade update' should not run the updateCheck

## 3.4.2 - 2019-1-18 - 981940b943cc198836483b35569de8e3e3721f38

- [BLADE-346]: Update to project templates
- [BLADE-361]: blade 'deploy' should behave more like traditional "deploy" tasks, i.e. its behavior should be obvious
- [BLADE-377]: init command should honor the -p, --profile-name flag over any defaults
- [BLADE-378]: update gives confusing message when switching to released version

## 3.4.1 - 2018-12-18 - 3cc0d3f734001578fc14ff0c51f3b38b6f7daff9

- [BLADE-245]: As a developer, I would to simplify the server command
- [BLADE-319]: blade create does not handle modules.dir property with multiple values
- [BLADE-354]: blade init will create new workspaces even if it is inside of another workspace without throwing an error
- [BLADE-356]: blade cli tests sometimes leave a leftover liferay server process running
- [BLADE-357]: Remove duplicate test classes from `maven-profile`
- [BLADE-358]: if I'm on a snapshot version, the automatic 'checkForUpdate' doesn't prompt if a new snapshot is available
- [BLADE-359]: Enable `blade update` functionality on Windows
- [BLADE-360]: Deploying modules created from project templates (OOB) fail using Blade
- [BLADE-366]: update project.templates dependency to 4.2.3
- [BLADE-367]: `maven-profile.jar` incorrectly being embedded inside itself
- [BLADE-369]: Update blade cli to use project templates 4.2.4
- [BLADE-370]: publish maven-profile snapshots
- [BLADE-371]: Add blade jar smoke tests
- [BLADE-372]: daily update check gives wrong message when using snapshots
- [BLADE-375]: update gradle tooling api to 4.10.2

## 3.3.0 - 2018-11-15 - 9939c44d9a1d981adfcae13ad168b6c63c2851d5

- [BLADE-214]: Create a `blade-extension` blade sample project, demonstrating how blade may be extended with custom commands
- [BLADE-313]: Add Maven support for `blade server`
- [BLADE-320]: Notify Blade users of updates via the CLI so they don't have to manually check
- [BLADE-321]: Strip version suffix of WAR artifact when it's deployed to Liferay Portal
- [BLADE-327]: Add `watch` command
- [BLADE-331]: Add Maven support for `blade server init`
- [BLADE-332]: Update to project templates 4.1.8
- [BLADE-333]: Publish Blade CLI JAR to Nexus
- [BLADE-334]: Add set method for `create` command argument
- [BLADE-335]: Modify `blade update` to install latest release from Nexus repo
- [BLADE-342]: Prompt Maven users to create a `.blade/settings.properties` file if it does not exist
- [BLADE-344]: Improve Blade errors to be more informative
- [BLADE-345]: Make extension path required for `blade extension install` command
- [BLADE-346]: Update to project templates 4.1.10
- [BLADE-347]: Include Maven profile classes in Blade CLI JAR
- [BLADE-349]: Skip project rebuilding when `blade watch` is executed
- [BLADE-350]: Update test projects to use same Gradle wrapper as Blade CLI's root project
- [BLADE-355]: Create release branch for Blade CLI
- [LRDOCS-5843]: Wordsmith README for building Blade profiles and extensions

## 3.2.0 - 2018-10-08 - 46b7d0a258376c9dccd5fecaae6156ed5a797d89

- [BLADE-214]: Create a `blade-extension` blade sample project, demonstrating how blade may be extended with custom commands
- [BLADE-286]: blade should be able to create module ext project
- [BLADE-294]: `blade open .` does not work
- [BLADE-296]: deploy for wars is swallowing output
- [BLADE-300]: the setter methods for InitArgs
- [BLADE-301]: Add set methods for Server related args
- [BLADE-302]: Investigate blade gw deploy
- [BLADE-304]: As a developer I want blade to remember what liferay version is set so I don't have to
- [BLADE-307]: Refactor extension tests into the extensions themselves
- [BLADE-308]: improve blade classloader for both embedding and custom commands
- [BLADE-309]: Commands with @BladeProfile are global
- [BLADE-310]: Open command fails when running with argument
- [BLADE-311]: Use a combined classloader for loading classes in blade
- [BLADE-314]: As a developer, I would like to get version specific samples
- [BLADE-315]: `blade server init` command for tooling-agnostic way to initialize the liferay-workspace
- [BLADE-323]: Create documentation for building blade profiles

## 3.1.2 - 2018-08-30 - ba8e7d64a866b4fbef383be37522d0eaf3bc8b4f

- [BLADE-256]: As a developer, i'd like tests to run in parallel
- [BLADE-265]: Print Gradle Errors in Test Results
- [BLADE-270]: blade init will fails if run in the empty folder
- [BLADE-271]: blade server start for finding the right server to start
- [BLADE-274]: blade deploy does not start wars on reinstall
- [BLADE-276]: blade init . doesn't create a workspace
- [BLADE-277]: `blade server stop` not recognizing folder names correctly
- [BLADE-280]: update to project templates 4.1.5
- [BLADE-281]: Creating project templates in workspace that has TP enabled should use TP enabled version of project template
- [BLADE-284]: update to project templates 4.1.6

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
[BLADE-245]: https://issues.liferay.com/browse/BLADE-245
[BLADE-246]: https://issues.liferay.com/browse/BLADE-246
[BLADE-250]: https://issues.liferay.com/browse/BLADE-250
[BLADE-251]: https://issues.liferay.com/browse/BLADE-251
[BLADE-253]: https://issues.liferay.com/browse/BLADE-253
[BLADE-256]: https://issues.liferay.com/browse/BLADE-256
[BLADE-257]: https://issues.liferay.com/browse/BLADE-257
[BLADE-258]: https://issues.liferay.com/browse/BLADE-258
[BLADE-259]: https://issues.liferay.com/browse/BLADE-259
[BLADE-260]: https://issues.liferay.com/browse/BLADE-260
[BLADE-262]: https://issues.liferay.com/browse/BLADE-262
[BLADE-264]: https://issues.liferay.com/browse/BLADE-264
[BLADE-265]: https://issues.liferay.com/browse/BLADE-265
[BLADE-270]: https://issues.liferay.com/browse/BLADE-270
[BLADE-271]: https://issues.liferay.com/browse/BLADE-271
[BLADE-274]: https://issues.liferay.com/browse/BLADE-274
[BLADE-276]: https://issues.liferay.com/browse/BLADE-276
[BLADE-277]: https://issues.liferay.com/browse/BLADE-277
[BLADE-280]: https://issues.liferay.com/browse/BLADE-280
[BLADE-281]: https://issues.liferay.com/browse/BLADE-281
[BLADE-284]: https://issues.liferay.com/browse/BLADE-284
[BLADE-286]: https://issues.liferay.com/browse/BLADE-286
[BLADE-294]: https://issues.liferay.com/browse/BLADE-294
[BLADE-296]: https://issues.liferay.com/browse/BLADE-296
[BLADE-300]: https://issues.liferay.com/browse/BLADE-300
[BLADE-301]: https://issues.liferay.com/browse/BLADE-301
[BLADE-302]: https://issues.liferay.com/browse/BLADE-302
[BLADE-304]: https://issues.liferay.com/browse/BLADE-304
[BLADE-307]: https://issues.liferay.com/browse/BLADE-307
[BLADE-308]: https://issues.liferay.com/browse/BLADE-308
[BLADE-309]: https://issues.liferay.com/browse/BLADE-309
[BLADE-310]: https://issues.liferay.com/browse/BLADE-310
[BLADE-311]: https://issues.liferay.com/browse/BLADE-311
[BLADE-313]: https://issues.liferay.com/browse/BLADE-313
[BLADE-314]: https://issues.liferay.com/browse/BLADE-314
[BLADE-315]: https://issues.liferay.com/browse/BLADE-315
[BLADE-319]: https://issues.liferay.com/browse/BLADE-319
[BLADE-320]: https://issues.liferay.com/browse/BLADE-320
[BLADE-321]: https://issues.liferay.com/browse/BLADE-321
[BLADE-323]: https://issues.liferay.com/browse/BLADE-323
[BLADE-327]: https://issues.liferay.com/browse/BLADE-327
[BLADE-331]: https://issues.liferay.com/browse/BLADE-331
[BLADE-332]: https://issues.liferay.com/browse/BLADE-332
[BLADE-333]: https://issues.liferay.com/browse/BLADE-333
[BLADE-334]: https://issues.liferay.com/browse/BLADE-334
[BLADE-335]: https://issues.liferay.com/browse/BLADE-335
[BLADE-342]: https://issues.liferay.com/browse/BLADE-342
[BLADE-343]: https://issues.liferay.com/browse/BLADE-343
[BLADE-344]: https://issues.liferay.com/browse/BLADE-344
[BLADE-345]: https://issues.liferay.com/browse/BLADE-345
[BLADE-346]: https://issues.liferay.com/browse/BLADE-346
[BLADE-347]: https://issues.liferay.com/browse/BLADE-347
[BLADE-349]: https://issues.liferay.com/browse/BLADE-349
[BLADE-350]: https://issues.liferay.com/browse/BLADE-350
[BLADE-354]: https://issues.liferay.com/browse/BLADE-354
[BLADE-355]: https://issues.liferay.com/browse/BLADE-355
[BLADE-356]: https://issues.liferay.com/browse/BLADE-356
[BLADE-357]: https://issues.liferay.com/browse/BLADE-357
[BLADE-358]: https://issues.liferay.com/browse/BLADE-358
[BLADE-359]: https://issues.liferay.com/browse/BLADE-359
[BLADE-360]: https://issues.liferay.com/browse/BLADE-360
[BLADE-361]: https://issues.liferay.com/browse/BLADE-361
[BLADE-366]: https://issues.liferay.com/browse/BLADE-366
[BLADE-367]: https://issues.liferay.com/browse/BLADE-367
[BLADE-369]: https://issues.liferay.com/browse/BLADE-369
[BLADE-370]: https://issues.liferay.com/browse/BLADE-370
[BLADE-371]: https://issues.liferay.com/browse/BLADE-371
[BLADE-372]: https://issues.liferay.com/browse/BLADE-372
[BLADE-373]: https://issues.liferay.com/browse/BLADE-373
[BLADE-375]: https://issues.liferay.com/browse/BLADE-375
[BLADE-377]: https://issues.liferay.com/browse/BLADE-377
[BLADE-378]: https://issues.liferay.com/browse/BLADE-378
[BLADE-380]: https://issues.liferay.com/browse/BLADE-380
[BLADE-381]: https://issues.liferay.com/browse/BLADE-381
[BLADE-383]: https://issues.liferay.com/browse/BLADE-383
[BLADE-385]: https://issues.liferay.com/browse/BLADE-385
[BLADE-386]: https://issues.liferay.com/browse/BLADE-386
[BLADE-387]: https://issues.liferay.com/browse/BLADE-387
[BLADE-388]: https://issues.liferay.com/browse/BLADE-388
[BLADE-389]: https://issues.liferay.com/browse/BLADE-389
[BLADE-390]: https://issues.liferay.com/browse/BLADE-390
[BLADE-392]: https://issues.liferay.com/browse/BLADE-392
[BLADE-394]: https://issues.liferay.com/browse/BLADE-394
[BLADE-395]: https://issues.liferay.com/browse/BLADE-395
[BLADE-397]: https://issues.liferay.com/browse/BLADE-397
[BLADE-398]: https://issues.liferay.com/browse/BLADE-398
[BLADE-407]: https://issues.liferay.com/browse/BLADE-407
[BLADE-409]: https://issues.liferay.com/browse/BLADE-409
[BLADE-411]: https://issues.liferay.com/browse/BLADE-411
[BLADE-412]: https://issues.liferay.com/browse/BLADE-412
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
[LRDOCS-5843]: https://issues.liferay.com/browse/LRDOCS-5843
[Updated Liferay Gradle Plugin]: https://github.com/liferay/liferay-portal/blob/master/modules/sdk/gradle-plugins/CHANGELOG.markdown#3523---2017-10-26