# Liferay Blade CLI Change Log

## 3.9.1 - 2020-02-25

### Commits
- [BLADE-520]: fix SF (fa386a3770)
- [BLADE-520]: Update test (07ee304d28)
- [BLADE-520]: Use com.liferay.gradle.plugins.workspace 2.2.6 (bd9dcedcc3)
- [BLADE-510]: use double quotes instead of single (f383241814)
- [BLADE-510]: Better dependency resolution process (9dca9979c0)
- [BLADE-513]: fix tests (5c09faa51f)
- [BLADE-513]: update tests to use 7.3 (1be4692714)
- [BLADE-513]: add support for 7.3 options (4e67f8bdaa)
- [BLADE-509]: fix tests (8849b673b6)
- [BLADE-509]: just always throw exception so it can be printed (37ab2e3521)
- [BLADE-509]: fix pluginsSdkDir checking logic (5eebe83726)
- [BLADE-509]: failing test case for convert command (d9462b3ee6)
- [BLADE-509]: convert command should be more careful when searching for plugin
source (37eefdc550)
- [BLADE-501 BLADE-504]: redundant (11911d0fde)
- [BLADE-501 BLADE-504]: Don't print error stacktrace for gradle executions we
just let original error be seen (cbe46d1073)
- [BLADE-501 BLADE-504]: Fix gradle test (3025ce27bc)
- [BLADE-504 BLADE-501]: Verify error and return code (f20fd778e6)
- [BLADE-501 BLADE-504]: Don't swallow gradle errors and return codes
(f70b6f0b92)
- [BLADE-505]: correctly exit script if tests fail (e4bc91df37)
- [BLADE-505]: Update to Project Templates 5.0.19 (16e16991d7)
- [BLADE-502]: Revert "BLADE-502 failing test cases" (92c464e071)
- [BLADE-502]: fix bash script (c624f08f61)
- [BLADE-502]: failing test cases (8d85b39126)
- [BLADE-502]: test scripts update (b75dc4970e)
- [BLADE-502]: archive tests if we have failures (5849a7545d)
- [BLADE-502]: ignore failing test on windows (5cfa62c3c1)
- [BLADE-502]: archive tests zip file if tests fail (eb46ca5417)
- [BLADE-418]: use ${project.version} (d50012c5a2)
- [BLADE-418]: normalize API (643fb00c33)
- [BLADE-418]: sort (28f0b9f504)
- [BLADE-418]: update template to gradle5 build (a4e791ca3e)
- [BLADE-502]: normalize var names (0d2d497ca3)
- [BLADE-502]: remove newline (7cfde50ffc)
- [BLADE-502]: Make Linux versions the same (238797209d)
- [BLADE-496]: Print error output (a02964a065)
- [BLADE-496]: ignore warnings (1efd53ce97)
- [BLADE-496]: Use built-in java for md5 (41a66690f6)
- [BLADE-502]: Add JDK 11 support to Azure Pipeline tests (7485b993aa)
- [BLADE-496]: Update dependencies for JDK11 (7a74f59534)
- [BLADE-496]: Update code to support JDK11 (18f7e3488f)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.15.
- [BLADE-520]: Update the com.liferay.project.templates dependency to version
5.0.25.
- [LPS-108630]: Update the com.liferay.project.templates dependency to version
5.0.23.
- [BLADE-510]: Update the ant dependency to version 1.10.7.
- [BLADE-513]: Update the com.liferay.project.templates dependency to version
5.0.21.
- [BLADE-507]: Update the com.liferay.project.templates dependency to version
5.0.20.
- [BLADE-505]: Update the com.liferay.project.templates dependency to version
5.0.19.
- []: Update the commons-compress dependency to version 1.18.
- [BLADE-496]: Update the powermock-api-easymock dependency to version 2.0.4.
- [BLADE-496]: Update the powermock-classloading-xstream dependency to version
2.0.4.
- [BLADE-496]: Update the powermock-module-junit4 dependency to version 2.0.4.
- [BLADE-496]: Update the powermock-module-junit4-rule dependency to version
2.0.4.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.15-SNAPSHOT.

## 3.9.0 - 2020-01-23

### Commits
- [BLADE-500]: Update to latest gradle-credentials-plugin (ee2ef0134e)
- [BLADE-499]: FS (df8f1e519c)
- [BLADE-499]: Fix failing test with updated workspace dependency (8838cbe91c)
- [BLADE-499]: Update to Project Templates 5.0.18 (0287e8d28f)
- [BLADE-493]: fix publish testing script (f43be7b73a)
- [BLADE-493]: fix jar basename (71e82140cb)
- [BLADE-493]: update to version 3.9.0-SNAPSHOT (3d54402e63)
- [BLADE-493]: fix sample test (9f49c33503)
- [BLADE-493]: remove 4.10.2 gradle wrapper (792e8980e1)
- [BLADE-493]: upate to gradle 5 API (39e33c13b1)
- [BLADE-493]: fix jar version (c67cd57df6)
- [BLADE-490]: update to gradle5 blade build (2d69c86d67)
- [LPS-105502]: correct tests (113f8c28c9)
- [LPS-105502]: no longer needed (32825d8f6a)
- [LPS-105502]: Use gradle 5 in project templates (2a58913f13)
- [BLADE-489]: Update Gradle Wrapper Version (bc84bbe6b3)
- [LRDOCS-7448]: Update description (32ff7c903d)
- [BLADE-487]: fix workspace plugin version in test to avoid collision
(fb1af68ae3)
- [BLADE-486]: don't autoclose FileSystem (b3092d41c8)
- [BLADE-486]: ignore eclipse bin folder (2defde4b3d)
- [BLADE-486]: logic improvements (f7e03f1921)
- [BLADE-486]: Use try-with-resources (5a7b7f1954)
- [BLADE-486]: anyMatch is more concise (6354fd2965)
- [BLADE-486]: emptyList is immutable (9669a2953d)
- [BLADE-486]: switch system.out/err to use blade streams (822b4a5193)
- [BLADE-486]: improve project searching capablities (0978956b4e)
- [BLADE-486]: make all arguments additive instead of overwriting (eeaf409fc7)
- [BLADE-486]: rename to --skip-init and default to false (6879c0136e)
- [BLADE-486]: add -f and -i shortnames (d71e4d42a6)
- [BLADE-486]: rename to use --fast-paths and --ignore-paths (6987982287)
- [BLADE-486]: ignore restriction (904af1a7b4)
- [BLADE-486]: gw formatSource (82fbaf8407)
- [BLADE-486]: optimize watch command for large gradle projects (36b22d2a97)
- [BLADE-486]: add classes to ignore (0f2be7b308)
- [BLADE-486]: provide option to skip initial deploy (75318a0cb2)
- [BLADE-486]: change watch command to use deploy and deployFast instead of
watch (6d0d3ba8cd)
- [BLADE-487]: semver (01320997b0)
- [BLADE-487]: load extensions from gradle or project objects (912a680eeb)
- [BLADE-487]: update tests with local workspace plugin (91a1021b06)
- [BLADE-487]: get dockerImageId and dockerContainerId from ProjectInfo model
(a2ea83322c)
- [BLADE-483]: simplfy and add API for getting snapshot/release version from
executed UpdateCommand (a4158e74f6)
- [BLADE-483]: Fix tests and display new releases for snapshots (c9be2fc8a4)
- [BLADE-483]: Fix unquoted string in publish.sh (f9589b82bd)
- [BLADE-483]: Change up2date checker (2eda4f4077)
- [BLADE-483]: UpdateCheck message changes (02891f3e0e)
- [BLADE-483]: Message fixes (0918e72d89)
- [BLADE-483]: Fix remaining two issues, snapshot to release and same version to
same version (e31ee23d87)
- [BLADE-483]: Refactor '_shouldUpdate' logic slightly (c12c38a385)
- [BLADE-483]: Fix minor typo, only check MD5 if major versions match
(8d10669129)
- [BLADE-483]: Clarify messages and fix snapshot comparison (061633a402)
- [BLADE-483]: Fix MD5 matching when using snapshots (ee1911b313)
- [BLADE-483]: Fix remaining issues and confusing errors (9fb48a2d7d)
- [BLADE-483]: Fix issue with snapshot updates (12940b9464)
- [BLADE-483]: rename class (dbc3b057dd)
- [BLADE-483]: remove eclipse warnings (198fa9e3e1)
- [BLADE-483]: add exit code back to correctly signal to calling process
(111b294308)
- [BLADE-483]: remove ignore (32e5fadc33)
- [BLADE-483]: rename (2febd5c8b3)
- [BLADE-483]: declare this variable closer to where it is used (6f16c8de7b)
- [BLADE-483]: rename (a28187e254)
- [BLADE-483]: make private constants (d49fe0bca3)
- [BLADE-483]: Refactor and update tests (01818d402b)
- [BLADE-483]: simpify and just use a predicate class (0b5267352e)
- [BLADE-483]: sort (f0d3b325c7)
- [BLADE-483]: Allow ability to skip uploading build scans (e4c2e36a15)
- [BLADE-483]: Fix tests (4ebd18594d)
- [BLADE-483]: update should stay on same branch (snapshot, release) unless
specified explicitly (1268818d27)
- [BLADE-483]: Implement new validation framework checking (617c2f6ea5)
- [BLADE-483]: Add validation to UpdateArgs and add new parameter (858ae20e11)
- [BLADE-483]: Add full argument validation framework (cda046eee1)
- [BLADE-483]: Assert BUILD SUCCESS only (aaba05c0ae)
- [BLADE-483]: Remove unnecessary test (895ac782b6)
- [BLADE-483]: Refactor unzipManifest (abc39235ab)
- [BLADE-483]: Fix Naming (28e801840c)
- [BLADE-483]: Remove unnecessary file (6e20c3d4d6)
- [BLADE-483]: Copy manifest in tests and use it to read version (7cd40959c3)
- [BLADE-483]: Make --check hidden (6f39d00ca6)
- [BLADE-483]: Switch if check in publish script (89b51c73bb)
- [BLADE-483]: Properly detect custom URL (f72a835bdb)
- [BLADE-483]: Add support for skipping tests (8f6e610a14)
- [BLADE-483]: Additional MD5 support. (72b92c7676)
- [BLADE-483]: Add MD5 Verification (c5265371df)
- [BLADE-475]: Fix gradle workspace version test (fb959c2927)
- [BLADE-475]: Use project templates 5.0.1 (a4c9bd668f)
- [BLADE-475]: Add Project Template 5.0 support (4da66ebfde)
- [BLADE-475]: initial work to adopt project templates 5.0.0 api (9bd91ea545)
- [BLADE-482]: Update to bnd 4.3.0 (fd824d780e)
- [BLADE-474]: simplify (7bae636837)
- [BLADE-474]: Support latest.integration and add a test (e92d01585a)
- [BLADE-474]: Simplify (c196815f64)
- [BLADE-474]: Properly detect workspace version as latest.release (abebcf4ce2)
- [BLADE-481]: Test (dd258e3e83)
- [BLADE-481]: Remove logic from TestUtil (1213ccb43a)
- [BLADE-481]: Add exception handling logic to TestUtil as well (3757349e00)
- [BLADE-481]: Move BridJ exception handling to BladeTest (be6a791f90)
- [BLADE-481]: Swap if statement (0361d6c8f9)
- [BLADE-481]: Ignore BridJ intermittent erroneous failure (12360baff4)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.14.
- [BLADE-499]: Update the com.liferay.project.templates dependency to version
5.0.18.
- [BLADE-493]: Update the gradle-base-services-groovy dependency to version
5.6.4.
- [BLADE-493]: Update the gradle-core dependency to version 5.6.4.
- [BLADE-493]: Update the gradle-tooling-api dependency to version 5.6.4.
- [BLADE-493]: Update the com.liferay.project.templates dependency to version
5.0.11.
- [LPS-105502]: Update the com.liferay.project.templates dependency to version
5.0.8.
- [BLADE-475]: Update the com.liferay.project.templates dependency to version
5.0.1.
- [BLADE-475]: Update the com.liferay.project.templates dependency to version
5.0.0.
- [BLADE-482]: Update the biz.aQute.bnd.gradle dependency to version 4.3.0.
- [BLADE-482]: Update the biz.aQute.bndlib dependency to version 4.3.0.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.14-SNAPSHOT.

## 3.8.0 - 2019-09-19

### Commits
- [BLADE-221]: Specifying invalid options should display an error (209be8edf6)
- [BLADE-221]: normalize (5fe764d620)
- [BLADE-221]: readd test (1d7b67c788)
- [BLADE-221]: rename to ParameterPossibleValues also change annotation to only
require a supplier (5a11159915)
- [BLADE-221]: rename to ParameterPossibleValues (034a2b730f)
- [BLADE-221]: use constants (713ae7c5d3)
- [BLADE-221]: Handle multiple main parameters by throwing ParameterException
(2fe92f82fa)
- [BLADE-221]: Fix Tests (13b126c316)
- [BLADE-221]: Refactor code (fe00fffbfe)
- [BLADE-221]: Fix smoke tests (339bb1d7d0)
- [BLADE-221]: Re-arrange arguments and re-word messages (9936df055d)
- [BLADE-221]: Add InputOptions annotation and validators (550b4967f2)
- [BLADE-221]: Fix tests, throw exception without interactive console
(1e5caec697)
- [BLADE-221]: Prompt for missing required arguments (924e053b1b)
- [BLADE-468]: Fix gradle test (00efddb3ae)
- [BLADE-468]: Fix extension sample tests (40c4b1e2a7)
- [BLADE-468]: Add -v option in maven tests (f0218f4cb4)
- [BLADE-468]: Add -v option in cli tests (86a35bcacc)
- [BLADE-468]: Always prompt user for Liferay Version when initializing a new
workspace (db19d04614)
- [BLADE-467]: Add test case to test targetplatform projects (d8fc4515e8)
- [BLADE-467]: Revert ffc6a0820c6977ac291cd6bd530a8d8e386f4261 (fa80ebeb0f)
- [LPS-98820]: Specify bnd.annotation in service builder templates (f1167725a5)
- [BLADE-464]: Bump minor version (a61951da6f)
- [BLADE-464]: Set project template args (c56f14c201)
- [BLADE-464]: Set default version to 7.2 (ffc6a0820c)
- [BLADE-464]: Typo in test (d21015f831)
- [BLADE-464]: Update test (114a785b76)
- [BLADE-464]: New parameters for Service builder template (5ec9cf6e07)
- [BLADE-464]: Add parameters for spring mvc portlet (2119eea2ec)
- [BLADE-464]: Moved FileUtil (3f846fe26b)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.13.
- []: Update the com.liferay.project.templates dependency to version 4.5.3.
- [LPS-98820]: Update the com.liferay.project.templates dependency to version
4.3.3.
- [BLADE-464]: Update the com.liferay.project.templates dependency to version
4.3.1.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.13-SNAPSHOT.

## 3.7.4 - 2019-07-23

### Commits
- [BLADE-461]: Use project templates 4.2.27 (591d642954)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.12.
- [BLADE-461]: Update the com.liferay.project.templates dependency to version
4.2.27.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.12-SNAPSHOT.

## 3.7.3 - 2019-06-24

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.11.
- []: Update the com.liferay.project.templates dependency to version 4.2.26.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.11-SNAPSHOT.

## 3.7.2 - 2019-06-19

### Commits
- [BLADE-441]: Use project-templates 4.2.25 to include naming standards fix
(036a43bc5b)
- [BLADE-452]: use canonical path (d8c7038513)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.10.
- [BLADE-441]: Update the com.liferay.project.templates dependency to version
4.2.25.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.10-SNAPSHOT.

## 3.7.1 - 2019-06-17

### Commits
- [BLADE-441]: Use project-templates 4.2.25 for naming standards fix
(5734917a79)
- [BLADE-400]: install blade into docker container and then test update to just
built version (5d68345667)
- [BLADE-400]: update url (772262ce7e)
- [BLADE-441]: Update InitCommandTest with updated links (9f3db4143e)
- [BLADE-441]: Upgraded to workspace 2.0.4 (d182be2aa8)
- [BLADE-441]: Use project-templates 4.2.24 for service builder TP fix
(40b70fa6a6)

### Dependencies
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.9.
- [BLADE-441]: Update the com.liferay.project.templates dependency to version
4.2.24.
- [BLADE-441]: Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.9-SNAPSHOT.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version latest.release.

## 3.7.0 - 2019-06-03

### Commits
- [BLADE-442]: fix the conflict between argument remove and custom name
(f69e58cfdb)
- [BLADE-436]: Add 7.2 back (cd68f33019)
- [BLADE-440]: Print maven output and execute all tasks at once (2bf8bce7c0)
- [BLADE-440]: Add @BladeProfile annotation to new command (fb5cd45705)
- [BLADE-440]: Add maven deploy support (e294148732)
- [BLADE-423]: fix issue (ce2d79ca15)
- [BLADE-423]: add previous constructor (658c858c61)
- [BLADE-423]: add parameter to enable not remove source project (f0426776cb)
- [BLADE-431]: Fix new test on Windows (f4361ebc9b)
- [BLADE-431]: Update project templates to 4.2.14 (4e898fc904)
- [BLADE-425]: Print warning if can't locate extension (b2f985b3f7)
- [BLADE-425]: string builder var names can be shortened to just sb (c3e17fce6b)
- [BLADE-425]: Add nested try-catch (911d547d9a)
- [BLADE-425]: Don't fail to load if an embedded extension fails (776942481e)
- [BLADE-408]: give a message if Node.JS is not installed (6f8d6e3b35)

### Dependencies
- []: Update the com.liferay.project.templates dependency to version 4.2.19.
- []: Update the com.liferay.project.templates dependency to version 4.2.17.
- []: Update the com.liferay.project.templates dependency to version 4.2.15.
- [BLADE-431]: Update the com.liferay.project.templates dependency to version
4.2.14.

## 3.6.0 - 2019-04-12

### Commits
- [BLADE-412]: Refactor and rename some packages (175d55882e)
- [BLADE-412]: publish changes for remote deploy (4c659dbfcc)
- [BLADE-412]: remove new lines (0f72dcab47)
- [BLADE-412]: rename var (05e5513bf2)
- [BLADE-412]: use as a list object since it matches underlying instance
(6db3e7ad65)
- [BLADE-412]: inline vars (c544cc7f66)
- [BLADE-412]: remove newlines (b16e5a4bec)
- [BLADE-412]: Refactor handling of git subdirectories (215f28bce5)
- [BLADE-412]: Deploy Remote Extension (48dea0e3ee)
- [BLADE-412]: Add ability to install extensions in subdirectories (3d5c657b47)
- [BLADE-411]: Remove newlines, add new filtering and refine buildService test
(a143f3f03f)
- [BLADE-411]: Ignore warnings (894f48f6bd)
- [BLADE-411]: Print Error (9744395241)
- [BLADE-411]: Remove new lines (0a569650ac)
- [BLADE-411]: Add buildService test (549a1568e5)
- [BLADE-411]: print exec args (1d9f422759)
- [BLADE-411]: rename method (9bffaacb8a)
- [BLADE-411]: extract to interface (90ae7eea89)
- [BLADE-411]: buildService maven command (b786922d69)
- [BLADE-409]: make sure that the length equals to 3 (0ebafe2287)
- [BLADE-409]: rename (d376ceb424)
- [BLADE-409]: support migration of portal jars in
liferay-plugin-package.properties (ead3a15fbf)
- [BLADE-407]: bump to 3.6.0-SNAPSHOT (eb21f04181)
- [BLADE-407]: add test (3c9223d937)
- [BLADE-407]: re-word (f1c99a6bf4)
- [BLADE-407]: add "source" parameter to convert command (e59c8df204)
- [BLADE-373]: remove from API (7adcce139d)
- [BLADE-373]: Rebase on master (84b0257083)
- [BLADE-373]: Change .blade/settings.properties to .blade.properties
(eb42f986ba)
- [BLADE-392]: suppress CDNCheck (013ad9e7a4)
- [BLADE-392]: inline (08d4b56aa2)
- [BLADE-398]: Blade Server Start / Run Custom Port Tests (7f3ae6cd89)
- [BLADE-392]: Fix Server Start / Run for Windows (8b4839323f)
- [BLADE-392]: Fix maven server run, refactor and improve tests, test server run
(d5442c070c)

## 3.5.0 - 2019-02-25

### Commits
- [BLADE-397]: Fix blade server start -t for wildfly (874081a134)
- [BLADE-389]: Improve Prompter and fix hang (52131fb22f)
- [BLADE-395]: -p, --port, -s, --suspend features added for blade server start
and run (633dc87461)
- [BLADE-394]: Use NIO, fix naming, final touches (4ac19ba2b6)
- [BLADE-394]: Refactor Server Init Test and naming conventions (d827882703)
- [BLADE-394]: Add Wildfly Debug Test (8226e2d0f9)
- [BLADE-394]: Add Wildfly Test Refactor (36f1e3b1f7)
- [BLADE-394]: Add Tomcat Debug Test (0adcedf1b8)
- [BLADE-394]: Refactor to support debug tests (f6a749973c)
- [BLADE-394]: Refactor Server Tests (799e09273d)
- [BLADE-390]: Parallel tests now require a -Pparallel flag (16d77e59fb)
- [BLADE-343]: bump to 3.5.0 (d9e4d322e3)
- [BLADE-343]: Improve naming (bd4dba3986)
- [BLADE-343]: Add Test (8c0fcf345f)
- [BLADE-343]: Pass in environment param when running blade server init
(6870cda3e2)
- [BLADE-387]: Add missing newline (8b24cf5716)
- [BLADE-387]: bump maven profile (bc73441294)
- [BLADE-387]: enable testing into local snapshot docker test (da41a4876b)
- [BLADE-387]: Fix up package naming (e86720fb66)
- [BLADE-387]: Bad Extension Test (1b4da19238)
- [BLADE-387]: Allow blade to run even if an extension doesn't load properly.
(45d5da54cc)
- [BLADE-388]: make snapshots testable (75d5f921f1)
- [BLADE-388]: only use mavenLocal for testing everything else should use normal
publishing to non-cdn repos (76e3374352)
- [BLADE-385]: rename (6c35bf7ff5)
- [BLADE-385]: simplify (ba6c606c84)
- [BLADE-385]: normalize (d1ebf9c7d7)
- [BLADE-385]: Fix CreateCommandMaven issue and test (71f04f976a)
- [BLADE-385]: Re-enable GadlePrintErrorTest (a730f31ebd)
- [BLADE-385]: Fix remaining tests (d5685ccdce)
- [BLADE-385]: re-enable test (ed87081e88)
- [BLADE-385]: ignore test (079a5ad7b8)
- [BLADE-385]: test no longer valid (e6fbd04c1e)
- [BLADE-385]: refactor classloader methods (3044fc9bff)
- [BLADE-385]: refactor WorkspaceProvider API (67946a0ffa)
- [BLADE-385]: ignore serial warning (56d8b58d7f)
- [BLADE-385]: remove cycle (1c7b6e46c4)
- [BLADE-385]: call run-tests.bat from appveyor (e48f832a1f)
- [BLADE-385]: fix windows bat (e2a2160bda)
- [BLADE-385]: enable scan (a2b67195d6)
- [BLADE-385]: fix SF (8835824c39)
- [BLADE-385]: script to run tests (1b2489a086)
- [BLADE-385]: apply SF to all projects (99197733eb)
- [BLADE-385]: normalize repos per project (69dc545b0b)
- [BLADE-385]: collect all smoke tests into task "smokeTests" (47fc60b431)
- [BLADE-385]: remove uneeded tests (45924edc63)
- [BLADE-385]: this is covered in smoke tests (2bc5246865)
- [BLADE-385]: remove mavenLocal (be8842ada3)
- [BLADE-385]: Better Profile Workspace handling / detection (ad808bb3a1)
- [BLADE-386]: use mvc-portlet and rename scripts (c9966fd3d3)
- [BLADE-386]: improve snapshots (785aa32491)
- [BLADE-386]: remove mavenRepo (be3ed2b6dd)
- [BLADE-386]: remove duplicate build tests (a5e70827fd)
- [BLADE-386]: these verification builds will be covered in project-templates
(3bbf4add5e)
- [BLADE-383]: read annotation instead of duplicating (9bc52e2492)
- [BLADE-383]: swith to private instead of protected (ce9f005448)
- [BLADE-383]: Fix unwanted stack traces (915760edcb)

### Dependencies
- []: Update the com.liferay.project.templates dependency to version 4.2.9.
- [BLADE-389]: Update the commons-io dependency to version 2.6.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version latest.integration.
- []: Update the com.liferay.blade.extensions.maven.profile dependency to
version 1.0.6+.

## 3.4.3 - 2019-01-22

### Commits
- [BLADE-377]: use two command build to ensure maven profile is included in
blade.jar (0fce29278f)
- [BLADE-377]: Add support for -b (9f8aa7e39e)
- [BLADE-381]: : 'blade update' should not run the updateCheck (1e7de5b825)
- [BLADE-380]: logic fix (055162245a)
- [BLADE-380]: allow maven init command to locate a workspace (4a3823c6e9)
- [BLADE-380]: switch to snapshots (cb5de58a13)
- [BLADE-380]: rename (ba67567805)
- [BLADE-380]: normalize variable names (bb96b8e733)
- [BLADE-380]: remove chaining (8979556456)
- [BLADE-380]: Refactor tests to use TestUtil (6c3fb96f5a)
- [BLADE-380]: Separate Settings and Extensions parent dirs (ca2a501d02)

## 3.4.2 - 2019-01-18

### Commits
- [BLADE-346]: Update project template version (95d4d123bc)
- [BLADE-377]: normalize variable names (93450b1453)
- [BLADE-377]: refactor command methods to BladeCLI (e8ca57cb59)
- [BLADE-377]: ignore /bin/ error (34d836319e)
- [BLADE-377]: Fix tests (b32c490347)
- [BLADE-377]: Fix Maven Profile and Tests (25d6061c2f)
- [BLADE-377]: Refactor to use -P, BladeCLI not depend on InitCommand
(9d4f0dd38f)
- [BLADE-377]: init command should honor the -p, --profile-name flag over any
defaults (276cd50b43)
- [BLADE-361]: unused (e26811c911)
- [BLADE-361]: Remove unused parameter (4828295c82)
- [BLADE-378]: rename (ef9f99e1a0)
- [BLADE-378]: : update gives confusing message when switching to released
version (06614a901e)
- [BLADE-361]: Fix Windows Test (0b81eecc29)
- [BLADE-361]: Refactor to always use deploy task (614ec60c7a)
- [BLADE-361]: Additional requested changes (00ca02d147)
- [BLADE-361]: Additional API change (b1084a9d07)
- [BLADE-361]: necessary API change (2e77dce549)
- [BLADE-361]: Additional requested changes (c9d529cf5b)
- [BLADE-361]: Blade Deploy API changes (5b18295bed)

### Dependencies
- [BLADE-346]: Update the com.liferay.project.templates dependency to version
4.2.6.

## 3.4.1 - 2018-12-18

### Commits
- [BLADE-319]: update sf dependency (9b671de13a)
- [BLADE-319]: simplify test (e8b94e1c1a)
- [BLADE-319]: reorder (288f81ab84)
- [BLADE-319]: : blade create does not handle modules.dir property with multiple
values (a090b10629)
- [BLADE-371]: rename file (a6f285b8b2)
- [BLADE-371]: configure smoke tests tasks by name (e66a4eb9da)
- [BLADE-371]: Change error parsing (fcb77fa489)
- [BLADE-371]: Fix task ordering (02f4665d23)
- [BLADE-371]: Simplify (bedb970df7)
- [BLADE-371]: Move version test (f5fa34bac3)
- [BLADE-371]: refactor tasks (5c2a352af3)
- [BLADE-371]: Add smoke tests (ac2e524743)
- [BLADE-375]: update gradle tooling api (0e40629668)
- [BLADE-375]: use specific version to speed up build (3cf16dfcd7)
- [BLADE-370]: Fix Maven Server Tests on Windows (3375cf46e7)
- [BLADE-370]: just mvnw should be enough (e83318ab8a)
- [BLADE-370]: add maven server start test (4ecb0665ee)
- [BLADE-370]: rename package (636caa3d9d)
- [BLADE-370]: add server run command maven (ea5b213fde)
- [BLADE-370]: Fix maven server commands (973796fa78)
- [BLADE-370]: Fix Maven Profile copying to folder (d61c327197)
- [BLADE-370]: encode blade extensions into properties file (1b3143a098)
- [BLADE-372]: wordsmith (cd25370249)
- [BLADE-372]: : daily update check gives wrong message when using snapshots
(762c98e576)
- [BLADE-354]: - blade init will create new workspaces even if it is inside of
another workspace without throwing an error (ad383e4f2a)
- [BLADE-370]: include maven-profile-1.0.0-SNAPSHOT.jar (f66a6f4356)
- [BLADE-369]: Use project templates 4.2.4 (da1c1bcfca)
- [BLADE-245]: rename (ad4fbf7ec6)
- [BLADE-245]: Refactor LocalServer (539d19f0d5)
- [BLADE-245]: igore spurious errors (668c46cd8e)
- [BLADE-245]: kill tomcat/wildfly first (304cc53192)
- [BLADE-245]: add bin (3093327337)
- [BLADE-245]: show process list (ba755b85a4)
- [BLADE-245]: add server run command and wildfly test (3a87d2c8cb)
- [BLADE-245]: create a specific StartServerCommandTest (5722cc69db)
- [BLADE-245]: use our own class to list current JVMs intead of 3rd party lib
(eb101b61e5)
- [BLADE-245]: refactor server run/stop commands (a208a15bd2)
- [BLADE-245]: extract new LocalServer command to encapsulate server behavior
(70089b48b9)
- [BLADE-245]: refactor into two commands blade server start, blade server run
(3c8540c517)
- [BLADE-245]: reduce uncessary API surface (780a13fa86)
- [BLADE-245]: refactor get args (95557696a6)
- [BLADE-367]: simplify (df473a3e8b)
- [BLADE-367]: rename task (e2e102ae2f)
- [BLADE-367]: maven-profile.jar fix recursive inclusion (7b43f67819)
- [BLADE-366]: Reduced number of templates (d6a45411c3)
- [BLADE-366]: Missing slash (4ddfa6d76d)
- [BLADE-366]: Format Source (b114dd0ad9)
- [BLADE-366]: Fix tests (ce9be9683c)
- [BLADE-366]: Update project templates to 4.2.3 (c47982e7f4)
- [BLADE-358]: : if I'm on a snapshot version, the automatic 'checkForUpdate'
doesn't prompt if a new snapshot is available. fix for shouldUpdate.
(2709e54a96)
- [BLADE-359]: prefer double quotes (06605a8c0b)
- [BLADE-359]: Fix Windows Update (64c73764fc)
- [BLADE-359]: rename (c28bbdec41)
- [BLADE-359]: wordsmith (f9bd3787b4)
- [BLADE-359]: better quote handling (eb37c5d053)
- [BLADE-359]: rename (bdb5c464b4)
- [BLADE-359]: Use a batch file for Blade Windows Update (fe4c6fd3fa)
- [BLADE-359]: Enable update functionality on Windows (a2e8281123)
- [BLADE-360]: Add missing classes to jar (077f5ecb46)
- [BLADE-358]: as used (031755bd24)
- [BLADE-358]: put variables closest to where they are needed (136491954d)
- [BLADE-358]: always write the update check (31f3fba596)
- [BLADE-358]: throw an error if we can't determine blade version (f0ebf082d7)
- [BLADE-358]: : if I'm on a snapshot version, the automatic 'checkForUpdate'
doesn't prompt if a new snapshot is available (0e745e1a86)
- [BLADE-357]: remove duplicate classes from maven-profile (0b4051ccb8)
- [BLADE-356]: Clean up lingering server process (5eec603229)

### Dependencies
- [BLADE-375]: Update the gradle-base-services-groovy dependency to version
4.10.2.
- [BLADE-375]: Update the gradle-core dependency to version 4.10.2.
- [BLADE-375]: Update the gradle-tooling-api dependency to version 4.10.2.
- [BLADE-370]: Update the com.liferay.blade.extensions.maven.profile dependency
to version latest.integration.
- [BLADE-369]: Update the com.liferay.project.templates dependency to version
4.2.4.
- [BLADE-366]: Update the com.liferay.project.templates dependency to version
4.2.3.
- [BLADE-356]: Update the javasysmon dependency to version 0.3.5.
- [BLADE-356]: Update the zt-process-killer dependency to version 1.9.

## 3.3.0 - 2018-11-15

### Commits
- [BLADE-355]: sort (4ba3d0fe93)
- [BLADE-335]: removed extra slashes (b921228e58)
- [BLADE-349]: call assemble and then watch with no-rebuild to make watch tasks
faster (f0e16e5b75)
- [BLADE-320]: rename (2e4d05cf94)
- [BLADE-320]: once a day, after each command is run, check for updates.
(1308de211f)
- [BLADE-335]: fix api (2380391d78)
- [BLADE-335]: parse xml doc instead of html (28618bb425)
- [BLADE-335]: add test (094d7efb7e)
- [BLADE-335]: use correct snapshot repo URL (0bdad5ab8d)
- [BLADE-335]: used error() instead of err() (a46a3ea281)
- [BLADE-335]: Simplify output (3351a7414a)
- [BLADE-335]: declare this variable as close to where it is actually needed as
possible. (ba01378c51)
- [BLADE-335]: normalize outputs (08d4c3d676)
- [BLADE-335]: keep URL related constants private so we don't overly polute the
public API (fa235191e5)
- [BLADE-335]: use bnd version class (aa7fe4d25e)
- [BLADE-335]: rename (ed3f3357a3)
- [BLADE-335]: use safer method toURI (a2a3602813)
- [BLADE-335]: use static field (ae1218dc1a)
- [BLADE-335]: rename (8500af01f7)
- [BLADE-335]: normalize version output (0bb491bc17)
- [BLADE-335]: use static method (e3295ff0e2)
- [BLADE-335]: add new logic for update (f6ebd3ee18)
- [BLADE-335]: make test names consistent. Refactor two tests for new update
logic. (5948f064b1)
- [BLADE-335]: make getBladeCLIVersion method static for use by UpdateCommand
(18d82bb50f)
- [BLADE-335]: add default value for bladeJarPath for testing in ide
(db74aea3f7)
- [BLADE-335]: use private constants (569c580851)
- [BLADE-335]: remove unneeded test (b17a996be8)
- [BLADE-335]: simplify (edb38395cf)
- [BLADE-335]: we can remove this now since we have a test class for it
(71e2179e50)
- [BLADE-335]: move this 'version' test into a VersionCommandTest (e936746234)
- [BLADE-335]: simplify tests (d655529727)
- [BLADE-335]: instead of hard-coding "build/lib/blade.jar" pass in the
jar.archivePath from gradle (caa741a68d)
- [BLADE-335]: rename test to UpdateCommandTest since it tests Updates
(93374f0fe1)
- [BLADE-335]: add new update API to UpdateCommand explicitly (82cd8ca24f)
- [BLADE-335]: add api to VersionCommand to get version explicitly (08cc6c7c90)
- [BLADE-335]: : blade update should install updates from nexus repository
(10ef779a22)
- [BLADE-342]: rename (3d21e92d30)
- [BLADE-342]: Use Scanner to read the input (90541c4b7e)
- [BLADE-347]: update paths (cdbba99d6d)
- [BLADE-347]: not needed (64aa94a90c)
- [BLADE-347]: move task configuration to afterEvaluate closure (9970f42afd)
- [BLADE-347]: move generated jar/zip files to buildDir (32975e22b6)
- [BLADE-347]: Better Resource Cleanup (6d3d330fe9)
- [BLADE-347]: Include maven-profile in default blade cli (55b6713ece)
- [BLADE-342]: provide default for prompt in case of migrating workspace
(acab346443)
- [BLADE-342]: remove while loop since it isn't clear it should be necessary
(c7ac8480bd)
- [BLADE-342]: not needed anymore (e6ceeac845)
- [BLADE-342]: rename method (4e969d603b)
- [BLADE-342]: changes requested and additional refactoring (7a42557da3)
- [BLADE-342]: prompt to create settings.properties if missing in maven
(5fec2e3350)
- [BLADE-333]: fix private package for tooling (802a822b42)
- [BLADE-344]: additional details (d5acddea22)
- [BLADE-347]: fix test (6f8e77a9f4)
- [BLADE-347]: rename (1302526d79)
- [BLADE-347]: Add getProperties() to CreateCommand (b1fbe95c7f)
- [LRDOCS-5843]: Fix links (0efee0c979)
- [LRDOCS-5843]: Final updates (24dd19234e)
- [LRDOCS-5843]: Wordsmithing (6d89f83259)
- [LRDOCS-5843]: Wrap 80 col and formatting (89fbce75a1)
- [BLADE-350]: update gradle wrapper version for test projects to same as root
project (b331515e03)
- [BLADE-334]: improve tooling.zip location handling (a289d52912)
- [BLADE-334]: update to gradlew in test-resources (d321c5db9b)
- [BLADE-334]: We should add set method for create command arg (1d6f407e06)
- [BLADE-346]: fix tests by adding liferay CDN repo to pom. (8ac3e1a3ab)
- [BLADE-346]: update project template version (ac762fc4ef)
- [BLADE-345]: make path required (f21d2c5821)
- [BLADE-344]: more informative and suggest --trace option (ffcbf94b88)
- [BLADE-321]: normalize var names (7e324a70cc)
- [BLADE-321]: Remove redundant path elements in message (593d823c77)
- [BLADE-321]: Fix war deploy with versions in filename (c1df6abf04)
- [BLADE-334]: We should add set method for create command arg (a21d5e93d4)
- [BLADE-333]: windows paths (552f71fe98)
- [BLADE-333]: refactor gradle tooling API (a08137f421)
- [BLADE-333]: reorganize gradle tooling model and enable snapshot publishing to
liferay nexus (160bb64464)
- [BLADE-332]: Use new Project Templates in Blade (4f88c60460)
- [BLADE-331]: improve error output (7beb762afc)
- [BLADE-331]: disable validation (ce61e0ece7)
- [BLADE-331]: use improved BladeTest instance instead (569f825377)
- [BLADE-331]: improve BladeTest error handling (53bab4755e)
- [BLADE-331]: improve error handling in tests (04ad00c6f6)
- [BLADE-331]: only deleteDir if exists (553cd10b0c)
- [BLADE-331]: remove unused (90dbffb765)
- [BLADE-331]: use deleteDir (0a19d03dbb)
- [BLADE-331]: sort (3817ff0fd7)
- [BLADE-331]: fix compile error (f22c0b9c26)
- [BLADE-331]: combine into one class (df5a31f3f7)
- [BLADE-331]: refactor to test class (3674825920)
- [BLADE-331]: rename (d8bef7f30a)
- [BLADE-331]: remove unneeded dependency (9548a032b3)
- [BLADE-331]: sort (a034fef4c0)
- [BLADE-331]: blade server init maven support (a1ef179bf6)
- [BLADE-327]: now a fix for linux :) (af3adc48f8)
- [BLADE-327]: better error msgs (d739be7f54)
- [BLADE-327]: fix windows (4cda6f5a79)
- [BLADE-327]: remove IO usage (8bdd4af4c9)
- [BLADE-327]: specify precise subset of org.gradle packages and improve init
script (056da92271)
- [BLADE-313]: Server Maven Support Fix (8d1e071862)
- [BLADE-313]: Server Start / Stop Maven Support (0becdc0ccf)
- [BLADE-214]: Maven Profile Fixes (4e8e203460)
- [BLADE-327]: new watch command prototype (bd2554d20a)
- [BLADE-327]: move SF setup into parent and apply SF (13b6789137)
- [BLADE-327]: refactor tooling model into sub projects (d1f853f953)
- [BLADE-323]: readme updates (46b7d0a258)
- [BLADE-323]: improvements (79ebf3bf56)

### Dependencies
- [BLADE-335]: Update the jsoup dependency to version 1.11.3.
- [BLADE-346]: Update the com.liferay.project.templates dependency to version
4.1.10.
- [BLADE-332]: Update the com.liferay.project.templates dependency to version
4.1.9.

## 3.2.0 - 2018-10-05

### Commits
- [BLADE-323]: update version to 3.2.0 (a07cf41d99)
- [BLADE-323]: initial blade extensions documentation (d5285c36e5)
- [BLADE-323]: add a sample workspace template to be used with a profile
(2be99a651c)
- [BLADE-323]: fix existing sample tests (943ee88edd)
- [BLADE-323]: add support for setting a profile with init (fa1f5c9a7a)
- [BLADE-315]: disable test on windows (023cd529b5)
- [BLADE-315]: we should just show gradle task output (f4f882e4c3)
- [BLADE-304]: rename (8098c6810e)
- [BLADE-304]: improve version handling (6cafdf6e5d)
- [BLADE-304]: set don't want to set the blade profile settings just because of
one create command choice (36bec8df66)
- [BLADE-304]: turns out the name liferayVersionDefault was better (618298f5c5)
- [BLADE-304]: Simplify (5e8c94c817)
- [BLADE-304]: Allow for multiple BladeSettings (2c64050c37)
- [BLADE-304]: Move logic out of main class (d0cc13f99f)
- [BLADE-304]: Remove default from name (3c9b8683bf)
- [BLADE-304]: Format Source (1df510fe5d)
- [BLADE-304]: Rename variable (7a253ac459)
- [BLADE-304]: Not needed (3b1a14a60a)
- [BLADE-304]: Revert samples changes (9592cee443)
- [BLADE-304]: Don't break backwards compatibility (03a8f54af3)
- [BLADE-304]: Fix standalone case (abe1be0a6f)
- [BLADE-304]: Samples command doesn't have liferayVersion arg (6dee41ea0d)
- [BLADE-304]: Add Tests (44838c39d1)
- [BLADE-304]: Refactor version saving (811bb9115f)
- [BLADE-304]: Get default Liferay version from settings.properties (1211ea9e1c)
- [BLADE-304]: Add setter and getter for default Liferay version (911bc2bfd2)
- [BLADE-304]: Add getters and setters (d65644041d)
- [BLADE-304]: Remove default version (167d2c0acc)
- [BLADE-304]: Write default Liferay version to settings.properties (d1f0a4118f)
- [BLADE-315]: Add a test (3568155c02)
- [BLADE-315]: use scanner (4a73fcee78)
- [BLADE-315]: wordsmith (cc673c8ba8)
- [BLADE-315]: Changes requested in review (3601ab0a90)
- [BLADE-315]: Fix tests (9af88f5049)
- [BLADE-315]: Server Init Command (7dd73e8bd0)
- [BLADE-214]: Maven Profile (3196f9185d)
- [BLADE-314]: var names (8f6f0a7579)
- [BLADE-314]: no need for field variables we can just use local versions
(593d612bc3)
- [BLADE-314]: Fix Tests and remove statics (b6eeb13cb8)
- [BLADE-314]: Add buildType test (caa265f841)
- [BLADE-314]: fix constants (f85156a2b5)
- [BLADE-314]: Add getter for SamplesArgs (aa645ba2c7)
- [BLADE-314]: Add tests (88928e73a6)
- [BLADE-314]: Pass in Liferay Version to Samples Command (a6f3872c0a)
- [BLADE-314]: Add Samples Args (d2b8fd004f)
- [BLADE-309]: Add test in a workspace (793cb90d96)
- [BLADE-309]: @BladeProfile restricted to workspaces (37d346c8fd)
- [BLADE-311]: rename and simplify (6430a2835e)
- [BLADE-311]: Make sure to use parent ClassLoader (14f65de111)
- [BLADE-310]: handle open parameter with no arg and add more logging
(a82ffabc4a)
- [BLADE-308]: remove unused (8f65a32043)
- [BLADE-308]: rename (77c610475b)
- [BLADE-308]: add a method instead of accesing field directly (3879e6586e)
- [BLADE-308]: rename (db3407cdf6)
- [BLADE-308]: combine classloaders (c760c8959a)
- [BLADE-305]: enable buildscan (cb9ee57d27)
- [BLADE-308]: SF new class (d9db20897a)
- [BLADE-308]: combine classloaders (40a75d04ff)
- [BLADE-307]: use compileOnly but also add project to testCompile (8f43fde684)
- [BLADE-307]: rename (3db73effa1)
- [BLADE-307]: Requested changes and SF (0378043b1d)
- [BLADE-307]: Refactor extension tests (259ce0526e)
- [BLADE-286]: change argument to -m -M options and update test so only -m is
required (4f107a1411)
- [BLADE-286]: create module ext project command (fdff6337c1)
- [BLADE-301]: Add set methods for Server related args (d7aae32c8e)
- [BLADE-300]: add setter methods for InitArgs (56fda51e50)
- [BLADE-296]: normalize output (20f49e0502)
- [BLADE-302]: for GradleWrapper command we should not capture IO (108a212a21)
- [BLADE-296]: Print Deploy Output (a1290d3743)
- [BLADE-294]: fix open option handling (6cd254b361)
- [BLADE-274]: Support updating and starting wars correctly from blade
(f966db3ada)
- [BLADE-214]: fix types (bf0c8e214f)
- [BLADE-288]: update create script to use 7.1 project templates (9b3d6ec89a)
- [BLADE-284]: update to project.templates 4.1.6 (80756b41cf)
- [BLADE-277]: improve test (5b0fd96ac0)
- [BLADE-277]: remove autoCloseable, not needed (6a348fcc3d)
- [BLADE-281]: fix initCommandTest can't compiled (76c3e1d596)
- [BLADE-281]: fix liferay-workspace-plugin version (1e1d98cdd4)
- [BLADE-276]: Fix blade init . (9acc0ba442)
- [BLADE-281]: update variable names and rename test methods (156ae84a65)
- [BLADE-281]: simplify (b192a283de)
- [BLADE-281]: simplify temp folders and rename files (78b60f3df4)
- [BLADE-281]: extract new WorkspaceUtil class (64b43f3861)
- [BLADE-281]: simplify pattern (231dff9b25)
- [BLADE-281]: use OSGi Version class instead of Maven ComparableVersion
(88dca11598)
- [BLADE-281]: rename variables (430aa4815c)
- [BLADE-281]: rename method (56b3f376f9)
- [BLADE-281]: support target platform for blade (d999f5e480)
- [BLADE-280]: add our nexus cdn to poms since not all artifacts are being
synced to central (4f78fd1b31)
- [BLADE-280]: update dependency to 4.1.5 (2b24fde06d)

### Dependencies
- [BLADE-286]: Update the com.liferay.project.templates dependency to version
4.1.7.
- [BLADE-284]: Update the com.liferay.project.templates dependency to version
4.1.6.
- [BLADE-277]: Update the zt-process-killer dependency to version 1.8.
- [BLADE-280]: Update the com.liferay.project.templates dependency to version
4.1.5.

## 3.1.2 - 2018-08-10

### Commits
- [BLADE-214]: fix test (d0732984c9)
- [BLADE-214]: update test (7d2f0177bb)
- [BLADE-214]: refator profile sample to 'new' and 'overridden' command
(4f143ba620)
- [BLADE-214]: Blade Profile Extension Example, test, and SF (4ed22e8ac0)
- [BLADE-277]: fix tests (3f26f113f3)
- [BLADE-277]: avoid wrapping and name variables based on complex type
(552f23be8d)
- [BLADE-277]: refactor to new ServerUtil class (491a677256)
- [BLADE-277]: Refactor to get reference to process (84e16c6fdd)
- [BLADE-277]: Test real commands (57049ab0d5)
- [BLADE-277]: Fix blade server stop and add tests (a0c68acb58)
- [BLADE-276]: Handle current directory better (d40ba3548f)
- [BLADE-271]: rename (56128cc5b2)
- [BLADE-271]: reduce api (eb2ad4fac7)
- [BLADE-271]: Better identification of server folders (ccffc9e988)
- [BLADE-265]: Fix Tests (e6a56e8a74)
- [BLADE-265]: rebased (1211e094d9)
- [BLADE-265]: Print Gradle Errors in Test Results (5db8a78b7d)
- [BLADE-270]: fix compile error (287fafe320)
- [BLADE-270]: reword (5ba05ae3bc)
- [BLADE-270]: rename (ae7109a82c)
- [BLADE-270]: use lamda (b3d03d96bc)
- [BLADE-270]: rename (d41757997b)
- [BLADE-270]: rename (d08fb7cf02)
- [BLADE-270]: Fix init and treat --base correctly (a419906bd9)
- [BLADE-256]: remove mocks since we can now perform a real uninstall
(3df42b8836)
- [BLADE-256]: refator BladeTest to have a parameter for userHomeDir that comes
from junit temporary Folder rule (3a650e54a9)
- [BLADE-256]: refactor extension path to come from blade settings class instead
of having a mutable userHomeDir on the Blade(Test) class. (6efb3c72f8)
- [BLADE-256]: rename (9823a07711)
- [BLADE-256]: : Run tests in parallel (99723bdf01)
- [BLADE-266]: clean out existing mavenRepo (44c6c89e14)
- [BLADE-262]: switch initArgs to 7.1 default (af8337cf9e)
- [BLADE-262]: update to project template 4.1.1 (5bc60eccab)
- [BLADE-264]: Fix tests (26ab09a9fe)
- [BLADE-264]: Update to Liferay Portal 7.1 (a9e9817f6f)
- [BLADE-260]: Handle Stream properly in in ServerStopCommand (d505a071ff)
- [BLADE-259]: rename (432dfe5191)
- [BLADE-259]: Fix NPE in ServerStopCommand (de1de0fb9a)
- [BLADE-257]: rename and SF (47b2985358)
- [BLADE-257]: Requested Changes (f7c82afc28)
- [BLADE-257]: Recognize commands with spaces (9e4b5383cc)

### Dependencies
- [BLADE-262]: Update the com.liferay.project.templates dependency to version
4.1.1.

## 3.1.1 - 2018-07-16

### Commits
- [BLADE-253]: update to 3.1.1 (5f659fd1e5)

## 3.1.0 - 2018-07-16

### Commits
- [BLADE-253]: specify version in gradle config (1ee2f9a03c)
- [BLADE-258]: update test (10f7260c07)
- [BLADE-258]: always print out errors don't require trace argument (cc9a81f143)
- [BLADE-258]: simplify (ba0851989b)
- [BLADE-258]: reword (91821d2a28)
- [BLADE-258]: Print blade deploy errors and honor --trace (9e6259b0ee)
- [BLADE-211]: configure changelog for BLADE project (b8f4b69004)
- [BLADE-211]: Apply changelog plugin (e192de3f58)
- [BLADE-250]: Interactive (77d4dfceb8)
- [BLADE-251]: Fix BladeUtil.findParentFile (a7711db76a)
- [BLADE-250]: Extensions Uninstall Fix Windows (0c6f43a11d)
- [BLADE-250]: Extension installed twice (5bc6a65b88)
- [BLADE-250]: failing test case (700dcfdab9)
- [BLADE-231]: refator to new class BladeSettings and use it to setup extensions
(c4d423b3f5)
- [BLADE-231]: sort gradle file (4b2bdb5205)
- [BLADE-231]: first time to use IntStream :) (276116febe)
- [BLADE-231]: Profile Work (67e3cad67c)
- [BLADE-231]: dont depend on internal class make our own copy (189c448e9e)
- [BLADE-231]: more specific (0a73d25ff0)
- [BLADE-231]: not needed (f588c9de05)
- [BLADE-231]: sort (89fb65380c)
- [BLADE-231]: Add github extension download test (95053de2c5)
- [BLADE-231]: Fix gradle build (8706745be8)
- [BLADE-231]: fix searching for windows (8cb92c2d4f)
- [BLADE-231]: remove unneeded file (f5530a8534)
- [BLADE-231]: switch to zip file (dac315de4a)
- [BLADE-231]: reorganize logic into isExtension which supports both templates
and commands (83d78dfea6)
- [BLADE-231]: reduce API (d1af533613)
- [BLADE-231]: remove mocks and test real sample command (97df7fd1df)
- [BLADE-231]: Fix test (0356beb9b5)
- [BLADE-231]: Format Source (4139070ac3)
- [BLADE-231]: Expand Custom Project Template Support (51c9743a1c)
- [BLADE-214]: formatting (8f498cdd47)
- [BLADE-214]: Sample Command Eclipse Compile fix (5f68fd5dd9)
- [BLADE-214]: Deploy Test Mock Fix (01834b9582)
- [BLADE-214]: add sample template (b72ca0ef96)
- [BLADE-214]: test fixes (6e50e2826b)
- [BLADE-214]: refactored into multi-module project (9739f5cb83)
- [BLADE-214]: refactor commands to new package (0955e5062e)
- [BLADE-214]: extract to new Extensions class (d57dd7a2e5)
- [BLADE-214]: always use _ instead of this (b48fcfcf42)
- [BLADE-214]: Update template commands after rebase (686850b0ef)
- [BLADE-214]: fix tests (032ddfd77e)
- [BLADE-214]: Custom Extension Support (7eb1f7eaeb)
- [BLADE-231]: fix test (af3021dabd)
- [BLADE-231]: add back (128dc4d7f2)
- [BLADE-231]: cleanup uninstall template (37d78ca730)
- [BLADE-231]: cleanup install template command (7d70dd92bf)
- [BLADE-231]: remove list template command (15988c424e)
- [BLADE-231]: Blade custom project template support (d2ae3f96b9)
- [BLADE-246]: reformat output message and add test (9780f996c2)
- [BLADE-246]: fix test (3b9f0a8f36)
- [BLADE-246]: Print warning when service template missing -s flag (e34e7a8259)
- [BLADE-244]: use bright green for STOPPED (cebe9341a8)
- [BLADE-244]: use patterns (7d5e222fed)
- [BLADE-244]: add colors to blade server start (be10d2e78f)
- [BLADE-235]: add more tests (5d01f1640c)
- [BLADE-235]: junit test for init with liferay version (0704f92bbd)
- [BLADE-235]: add liferay version support for blade init command (05a38ecee7)
- [BLADE-233]: don't chain (97e376315f)
- [BLADE-233]: fix test (f7c0dbea2f)
- [BLADE-233]: better blade deploy message (9824459992)
- [BLADE-233]: blade deploy causes error (1ef07e0aa5)
- [BLADE-228]: fix test (c93f7eb2a9)
- [BLADE-228]: remove maven local (1bc98d6b48)
- [BLADE-228]: update test (427b69b688)
- [BLADE-228]: don't need this (dda746c6d9)
- [BLADE-228]: blade create should support specifying the Liferay Version
(5a4e1cae05)
- [BLADE-228]: Add mavenLocal as explicit repository (bad768580c)
- [BLADE-228]: Add JCommander as explicit dependency (d1c90ec5ae)
- [BLADE-230]: make more friendly (88d9263e76)
- [BLADE-230]: use new constructor to pass printStreams (c15f5e1870)
- [BLADE-230]: Fix ordering (7ecd40afa8)
- [BLADE-230]: Changes requested (06b48b3292)
- [BLADE-230]: blade -t should be required (a93927cfcd)
- [BLADE-227]: formatting (ee3652e57f)
- [BLADE-227]: Make general help less verbose (bc03e5c972)
- [BLADE-226]: rename (25efe8effe)
- [BLADE-226]: Blade creation of fragment requires -h and -H (203bc2b080)
- [BLADE-219]: formatting (6d54088226)
- [BLADE-219]: Fix Server Start Command (a05f90df29)
- [BLADE-216]: review and formatting (33b8b2b378)
- [BLADE-190]: : Fix Deploy Tests (9b14dbe1f2)
- [BLADE-190]: : Fix some issues with deploy (debc2f850a)
- [BLADE-190]: : Refactor code and tests (377fadddd6)
- [BLADE-190]: rename (968f5fb370)
- [BLADE-190]: update deploy test with example jar and war files (9488fb4ddb)
- [BLADE-190]: refactor deploy to use gogotelnetclient and domain classes
(3aac877678)
- [BLADE-190]: : blade to support deploying wars to Liferay (3e5d7e1595)
- [BLADE-209]: : Restore Help Command (b7a8efd0e5)
- [BLADE-212]: fix blade init command failed for no destination (1e41cef115)
- [BLADE-208]: review (59de71b72c)
- [BLADE-208]: : Restore Version Command (f69cf16719)
- [BLADE-199]: reword and source format (d8d695eb6a)
- [BLADE-199]: add new test for gradle build option (07acd77402)
- [BLADE-199]: add creating maven workspace support (437eeee06a)
- [BLADE-210]: : Fix Project Creation Path (2de6002a8e)
- [BLADE-210]: : Fix Project Creation Path (79ef9915f9)
- [BLADE-206]: make project specific (8325bacabc)
- [BLADE-206]: : Wars in wars folder (01f7a0ede9)
- [BLADE-202]: : Use JCommander (b08d285090)

### Dependencies
- [BLADE-211]: Update the com.liferay.gradle.plugins.change.log.builder
dependency to version 1.1.0.
- []: Update the diffutils dependency to version 1.3.0.
- []: Update the zipdiff dependency to version 1.0.
- []: Update the com.liferay.project.templates dependency to version 4.1.0.
- []: Update the com.liferay.project.templates dependency to version 4.0.1.
- []: Update the powermock-classloading-xstream dependency to version 1.7.3.
- []: Update the powermock-module-junit4-rule dependency to version 1.7.3.
- [BLADE-214]: Update the biz.aQute.bnd.gradle dependency to version 3.5.0.
- [BLADE-214]: Update the com.liferay.gradle.plugins.source.formatter dependency
to version latest.release.
- [BLADE-214]: Update the gradle-download-task dependency to version 3.3.0.
- [BLADE-214]: Update the aQute.libg dependency to version 3.5.0.
- [BLADE-214]: Update the biz.aQute.bndlib dependency to version 3.5.0.
- [BLADE-214]: Update the com.liferay.gogo.shell.client dependency to version
1.0.0.
- [BLADE-214]: Update the com.liferay.project.templates dependency to version
4.0.0.
- [BLADE-214]: Update the commons-io dependency to version 2.5.
- [BLADE-214]: Update the asciitable dependency to version 0.3.2.
- [BLADE-214]: Update the commons-lang3 dependency to version 3.4.
- [BLADE-214]: Update the maven-aether-provider dependency to version 3.3.9.
- [BLADE-214]: Update the maven-settings dependency to version 3.3.9.
- [BLADE-214]: Update the maven-settings-builder dependency to version 3.3.9.
- [BLADE-214]: Update the aether-api dependency to version 1.0.2.v20150114.
- [BLADE-214]: Update the aether-connector-basic dependency to version
1.0.2.v20150114.
- [BLADE-214]: Update the aether-impl dependency to version 1.0.2.v20150114.
- [BLADE-214]: Update the aether-spi dependency to version 1.0.2.v20150114.
- [BLADE-214]: Update the aether-transport-classpath dependency to version
1.0.2.v20150114.
- [BLADE-214]: Update the aether-transport-file dependency to version
1.0.2.v20150114.
- [BLADE-214]: Update the aether-transport-http dependency to version
1.0.2.v20150114.
- [BLADE-214]: Update the aether-transport-wagon dependency to version
1.0.2.v20150114.
- [BLADE-214]: Update the aether-util dependency to version 1.0.2.v20150114.
- [BLADE-214]: Update the jansi dependency to version 1.17.1.
- [BLADE-214]: Update the gradle-base-services-groovy dependency to version 3.0.
- [BLADE-214]: Update the gradle-core dependency to version 3.0.
- [BLADE-214]: Update the gradle-tooling-api dependency to version 3.0.
- [BLADE-214]: Update the osgi.core dependency to version 6.0.0.
- [BLADE-214]: Update the junit dependency to version 4.12.
- [BLADE-214]: Update the easymock dependency to version 3.5.1.
- [BLADE-214]: Update the osgi.core dependency to version 6.0.0.
- [BLADE-214]: Update the powermock-api-easymock dependency to version 1.7.3.
- [BLADE-214]: Update the powermock-module-junit4 dependency to version 1.7.3.

## 2.1.0 - 2017-06-14

### Commits
- [LRDOCS-3688]: README should now point to official docs for easier
maintainability (7f9bc3f45e)
- [BLADE-151]: bump to 2.1.1 (c8c6805d4d)
- [BLADE-151]: remove reference to older sdk file (dc6a4e7243)
- [BLADE-151]: switch to 1.0.9 (e7bfba6d7b)
- [IDE-3167]: avoid checking content empty file (aa20ee9740)

## 2.0.2 - 2017-04-04

### Commits
- [LPS-50156]: and LPS-54798 (49d4bd764c)

## 2.0.0 - 2017-01-13

### Commits
- [IDE-3043]: fix infinitely pluings-sdk dir copying (3ecda4607b)
- [LPS-58672]: ) (d4e6b36544)
- [LPS-47580]: (Moved Recycle Bin Logic Into a New DLTrashService Interface)
(818a2fb19c)

## 1.2.0 - 2016-10-14

### Commits
- [LPS-50156]: problem (dc18d4431e)
- [LPS-67504]: add tests (512a3208b8)
- [LPS-67504]: Update version for template bundle that has api (b4a1085218)
- [LPS-65477]: update to gradle templates 1.0.18 that contains bug fix
(d068cfb6aa)
- [LPS-65477]: add tests (385cedb3ac)
- [LPS-65477]: add new blade templates (582f305e00)

## 1.0.1 - 2016-07-20

### Commits
- [IDE-2756]: fix fragment deploying error (cc949d5a97)

[//]: Markdown Links
[BLADE-151]: https://issues.liferay.com/browse/BLADE-151
[BLADE-190]: https://issues.liferay.com/browse/BLADE-190
[BLADE-199]: https://issues.liferay.com/browse/BLADE-199
[BLADE-202]: https://issues.liferay.com/browse/BLADE-202
[BLADE-206]: https://issues.liferay.com/browse/BLADE-206
[BLADE-208]: https://issues.liferay.com/browse/BLADE-208
[BLADE-209]: https://issues.liferay.com/browse/BLADE-209
[BLADE-210]: https://issues.liferay.com/browse/BLADE-210
[BLADE-211]: https://issues.liferay.com/browse/BLADE-211
[BLADE-212]: https://issues.liferay.com/browse/BLADE-212
[BLADE-214]: https://issues.liferay.com/browse/BLADE-214
[BLADE-216]: https://issues.liferay.com/browse/BLADE-216
[BLADE-219]: https://issues.liferay.com/browse/BLADE-219
[BLADE-221]: https://issues.liferay.com/browse/BLADE-221
[BLADE-226]: https://issues.liferay.com/browse/BLADE-226
[BLADE-227]: https://issues.liferay.com/browse/BLADE-227
[BLADE-228]: https://issues.liferay.com/browse/BLADE-228
[BLADE-230]: https://issues.liferay.com/browse/BLADE-230
[BLADE-231]: https://issues.liferay.com/browse/BLADE-231
[BLADE-233]: https://issues.liferay.com/browse/BLADE-233
[BLADE-235]: https://issues.liferay.com/browse/BLADE-235
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
[BLADE-266]: https://issues.liferay.com/browse/BLADE-266
[BLADE-270]: https://issues.liferay.com/browse/BLADE-270
[BLADE-271]: https://issues.liferay.com/browse/BLADE-271
[BLADE-274]: https://issues.liferay.com/browse/BLADE-274
[BLADE-276]: https://issues.liferay.com/browse/BLADE-276
[BLADE-277]: https://issues.liferay.com/browse/BLADE-277
[BLADE-280]: https://issues.liferay.com/browse/BLADE-280
[BLADE-281]: https://issues.liferay.com/browse/BLADE-281
[BLADE-284]: https://issues.liferay.com/browse/BLADE-284
[BLADE-286]: https://issues.liferay.com/browse/BLADE-286
[BLADE-288]: https://issues.liferay.com/browse/BLADE-288
[BLADE-294]: https://issues.liferay.com/browse/BLADE-294
[BLADE-296]: https://issues.liferay.com/browse/BLADE-296
[BLADE-300]: https://issues.liferay.com/browse/BLADE-300
[BLADE-301]: https://issues.liferay.com/browse/BLADE-301
[BLADE-302]: https://issues.liferay.com/browse/BLADE-302
[BLADE-304]: https://issues.liferay.com/browse/BLADE-304
[BLADE-305]: https://issues.liferay.com/browse/BLADE-305
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
[BLADE-400]: https://issues.liferay.com/browse/BLADE-400
[BLADE-407]: https://issues.liferay.com/browse/BLADE-407
[BLADE-408]: https://issues.liferay.com/browse/BLADE-408
[BLADE-409]: https://issues.liferay.com/browse/BLADE-409
[BLADE-411]: https://issues.liferay.com/browse/BLADE-411
[BLADE-412]: https://issues.liferay.com/browse/BLADE-412
[BLADE-418]: https://issues.liferay.com/browse/BLADE-418
[BLADE-423]: https://issues.liferay.com/browse/BLADE-423
[BLADE-425]: https://issues.liferay.com/browse/BLADE-425
[BLADE-431]: https://issues.liferay.com/browse/BLADE-431
[BLADE-436]: https://issues.liferay.com/browse/BLADE-436
[BLADE-440]: https://issues.liferay.com/browse/BLADE-440
[BLADE-441]: https://issues.liferay.com/browse/BLADE-441
[BLADE-442]: https://issues.liferay.com/browse/BLADE-442
[BLADE-452]: https://issues.liferay.com/browse/BLADE-452
[BLADE-461]: https://issues.liferay.com/browse/BLADE-461
[BLADE-464]: https://issues.liferay.com/browse/BLADE-464
[BLADE-467]: https://issues.liferay.com/browse/BLADE-467
[BLADE-468]: https://issues.liferay.com/browse/BLADE-468
[BLADE-474]: https://issues.liferay.com/browse/BLADE-474
[BLADE-475]: https://issues.liferay.com/browse/BLADE-475
[BLADE-481]: https://issues.liferay.com/browse/BLADE-481
[BLADE-482]: https://issues.liferay.com/browse/BLADE-482
[BLADE-483]: https://issues.liferay.com/browse/BLADE-483
[BLADE-486]: https://issues.liferay.com/browse/BLADE-486
[BLADE-487]: https://issues.liferay.com/browse/BLADE-487
[BLADE-489]: https://issues.liferay.com/browse/BLADE-489
[BLADE-490]: https://issues.liferay.com/browse/BLADE-490
[BLADE-493]: https://issues.liferay.com/browse/BLADE-493
[BLADE-496]: https://issues.liferay.com/browse/BLADE-496
[BLADE-499]: https://issues.liferay.com/browse/BLADE-499
[BLADE-500]: https://issues.liferay.com/browse/BLADE-500
[BLADE-501]: https://issues.liferay.com/browse/BLADE-501
[BLADE-502]: https://issues.liferay.com/browse/BLADE-502
[BLADE-504]: https://issues.liferay.com/browse/BLADE-504
[BLADE-505]: https://issues.liferay.com/browse/BLADE-505
[BLADE-507]: https://issues.liferay.com/browse/BLADE-507
[BLADE-509]: https://issues.liferay.com/browse/BLADE-509
[BLADE-510]: https://issues.liferay.com/browse/BLADE-510
[BLADE-513]: https://issues.liferay.com/browse/BLADE-513
[BLADE-520]: https://issues.liferay.com/browse/BLADE-520
[IDE-2756]: https://issues.liferay.com/browse/IDE-2756
[IDE-3043]: https://issues.liferay.com/browse/IDE-3043
[IDE-3167]: https://issues.liferay.com/browse/IDE-3167
[LPS-47580]: https://issues.liferay.com/browse/LPS-47580
[LPS-50156]: https://issues.liferay.com/browse/LPS-50156
[LPS-58672]: https://issues.liferay.com/browse/LPS-58672
[LPS-65477]: https://issues.liferay.com/browse/LPS-65477
[LPS-67504]: https://issues.liferay.com/browse/LPS-67504
[LPS-98820]: https://issues.liferay.com/browse/LPS-98820
[LPS-105502]: https://issues.liferay.com/browse/LPS-105502
[LPS-108630]: https://issues.liferay.com/browse/LPS-108630
[LRDOCS-3688]: https://issues.liferay.com/browse/LRDOCS-3688
[LRDOCS-5843]: https://issues.liferay.com/browse/LRDOCS-5843
[LRDOCS-7448]: https://issues.liferay.com/browse/LRDOCS-7448