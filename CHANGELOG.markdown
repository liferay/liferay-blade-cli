# Liferay Blade CLI Change Log

## 7.0.2 - 2024-11-11

### Commits
- [LPD-41339] liferay-blade-cli: workflow: parameterize the jobs that are
otherwise identical except for JDK version (35f153225c)
- [LPD-41339] liferay-blade-cli: workflow: bumps version of upload-artifact
(ab74e7df78)
- [LPD-41339] liferay-blade-cli: put jcenter last (4b4eadc9eb)
- [LPD-32781] cli: bnd.bnd: includes org.apache.http at runtime (b2bb226c16)
- [LPD-32781] cli: bumps com.liferay.project.templates to 5.0.297 (c322469341)
- [LPD-32781] cli: use string matching on the docker property to get the target
platform version from the docker image property (49dba7b3f0)
- [LPD-32781] liferay-blade-cli: auto SF (57243dd6f6)
- [LPD-32781] extensions: bumps project templates compatible version ranges
(b124de1dbe)
- [LPD-32781] liferay-blade-cli: removes unused ResourceUtil (dafdd33d09)
- [LPD-32781] liferay-blade-cli: CreateCommand: normalizes version string before
checking Version range (da782b67f6)
- [LPD-32781] liferay-blade-cli: updates getProduct methods in WorkspaceProvider
classes (ca01fea6c5)
- [LPD-32781] liferay-blade-cli: updates usages to use new lib and util in tests
(d145cd6899)
- [LPD-32781] liferay-blade-cli: updates usages to use new lib and util
(c7bcbf9321)
- [LPD-32781] liferay-blade-cli: ReleaseUtil extends from lib ReleaseUtil
(3d8c2ebc81)
- [LPD-32781] includes lib classes in resulting jar (0e77920e1c)
- [LPD-32781] adds com.liferay.release.util dependency (dcb3cc70d9)
- [LPD-32781] cli: bumps project templates dependency version (45263216e5)

### Dependencies
- [LPD-41339] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.42.
- [LPD-41339] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.25.
- [LPD-41339] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.26.
- [LPD-32781] Update the com.liferay.project.templates dependency to version
5.0.298.
- [LPD-32781] Update the com.liferay.release.util dependency to version 1.0.0.
- [LPD-32781] Update the com.liferay.project.templates dependency to version
5.0.296.
- [LPD-32144] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.42-SNAPSHOT.
- [LPD-32144] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.25-SNAPSHOT.
- [LPD-32144] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.26-SNAPSHOT.

## 7.0.1 - 2024-07-26

### Commits
- [LPD-31577] liferay-blade-cli: bumps expected version of workspace in tests
(4305a9108a)
- [LPD-31577] liferay-blade-cli: bumps version of project templates (54bd599f66)
- [BLADE-750] cli: fixes Gradle 8 implicit dependency error (6404a33a6a)
- [BLADE-750] cli: temp ignores test case while Project Templates are being
updated (48fb3b724e)
- [BLADE-750] bnd.bnd: fixes missing class error (7ac3d4b1f4)
- [BLADE-750] liferay-blade-cli: move to Gradle 8.4 - gw wrapper --version 8.4
(c54c1b7b35)
- [BLADE-750] cli: allows extra properties (6cfef29ced)
- [LPD-28297] cli: removes sorting test case since Blade no longer controls
sorting (890266a836)

### Dependencies
- [LPD-32144] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.41.
- [LPD-32144] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.24.
- [LPD-32144] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.25.
- [LPD-31577] Update the com.liferay.project.templates dependency to version
5.0.295.
- [LPD-28297] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.41-SNAPSHOT.
- [LPD-28297] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.24-SNAPSHOT.
- [LPD-28297] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.25-SNAPSHOT.

## 7.0.0 - 2024-06-11

### Commits
- [LPD-28093] cli: test: fixes expected version since 7.0 is no longer present
in the releases.json (3f93e5965f)
- [LPD-28093] cli: test: fixes SF error (6fa573e740)
- [LPD-28093] cli: test: updates expected workspace version (77df699733)
- [LPD-28093] cli: uses project templates version 5.0.293 (8b78c3f0a8)
- [LPD-28093] liferay-blade-cli: bumps snapshot version to 7.0.0 (e3683ae545)

### Dependencies
- [LPD-28297] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.40.
- [LPD-28297] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.23.
- [LPD-28297] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.24.
- [LPD-28093] Update the com.liferay.project.templates dependency to version
5.0.293.
- [BLADE-745] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.40-SNAPSHOT.
- [BLADE-745] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.23-SNAPSHOT.
- [BLADE-745] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.24-SNAPSHOT.

## 6.0.0 - 2024-04-09

### Commits
- [BLADE-744] liferay-blade-cli: updates README.markdown (97af22f2bf)
- [BLADE-743] liferay-blade-cli: removes references to
project-templates-freemarker-portlet (eedb89e157)
- [BLADE-743] extensions: removes project-templates-freemarker-portlet directory
(767f913a38)
- [BLADE-743] liferay-blade-cli: removes project-templates-freemarker-portlet
project (e6609b536c)
- [BLADE-743] liferay-blade-cli: removes references to
project-templates-activator (5de01e90a1)
- [BLADE-743] extensions: removes project-templates-activator directory
(8858bac075)
- [BLADE-743] liferay-blade-cli: settings.gradle: removes
project-templates-activator project (931eef5375)
- [BLADE-743] cli: BladeTest: temporarily hard-codes the value (f4911ea824)
- [BLADE-743] liferay-blade-cli: StringUtil: fixes NPE - allows null strings
(ea8883aee7)
- [BLADE-743] liferay-blade-cli: BladeCLI: simplify (a9dfccae98)
- [BLADE-743] liferay-blade-cli: ReleaseUtil: initialize releases if none are
found (386284c9a2)
- [BLADE-743] liferay-blade-cli: ServerStartCommandTest: apply Gradle memory fix
(b8c3f214b5)
- [BLADE-743] liferay-blade-cli: CreateCommandTest: update usage (1bef0138c5)
- [BLADE-743] liferay-blade-cli: TestUtil: adds helper methods for updating
Gradle properties (e031f4f2cf)
- [BLADE-743] cli: ReleaseUtil: adjusts priority, and adds an option for an
environment var (664823dde6)
- [BLADE-743] cli: BladeCLI: ResourceUtil follows the trace arg (40df81fcd4)
- [BLADE-743] cli: ArrayUtil: adds contains helper method (ac30f96335)
- [BLADE-743] extensions: SampleTemplatesTest: fixes test case (712de2d3a5)
- [BLADE-743] cli: updates expected values in smoke tests (2fb3877b67)
- [BLADE-743] cli: ReleaseUtil: removes unused methods and classes (6b64416129)
- [BLADE-743] cli: BladeTest: removes unused field (eb63e348b7)
- [BLADE-743] cli: removes unused ProductKey classes (064a52e19c)
- [BLADE-743] cli: InitCommand: use fallback matching on the version
(77211e2843)
- [BLADE-743] extensions: bumps com.liferay.project.templates.extensions
dependency version (ee13e989ab)
- [BLADE-743] liferay-blade-cli: removes references to deleted extensions
(9638e81984)
- [BLADE-743] extensions: removes project-templates-client-extension
(327816f3cf)
- [BLADE-743] extensions: removes content-targeting extensions (ac9d5617f9)
- [BLADE-743] liferay-blade-cli: build.gradle: major version bump (96bf2a919b)
- [BLADE-743] cli: LiferayMoreVersionValidatorTest: fixes test cases
(63a6c7653b)
- [BLADE-743] cli: ServerStartCommandTest: fixes test case (66403e64f7)
- [BLADE-743] cli: SamplesCommandTest: ignores tests that break because of the
Gradle 8 updates (949214e649)
- [BLADE-743] cli: CreateCommandTest: fix the heap size failure (51d31ed06b)
- [BLADE-743] cli: CreateCommandTest: fixes test cases (109f69bbc8)
- [BLADE-743] cli: UtilTest: fixes test cases (1b05f9c561)
- [BLADE-743] cli: InitCommandTest: fixes test cases (fd4596342a)
- [BLADE-743] cli: BladeTest: adds helper method and ensures that the constants
are reading live values (74efac8666)
- [BLADE-743] cli: JavaProcesses: source formatting (de19eb8644)
- [BLADE-743] cli: BladeUtil: removes unused methods and fields (e7575bb556)
- [BLADE-743] extensions: ClientExtensionProjectTemplateCustomizer: uses
ReleaseUtil (fb11aaf187)
- [BLADE-743] cli: GradleWorkspaceProvider: uses ReleaseUtil (b659fbceca)
- [BLADE-743] cli: LiferayMoreVersionValidator: uses ReleaseUtil (1d2e6c1436)
- [BLADE-743] cli: LiferayDefaultVersionValidator: uses ReleaseUtil (4bbc561d74)
- [BLADE-743] cli: InitCommand: uses ReleaseUtil (602c08be8d)
- [BLADE-743] cli: CreateCommand: provides exception for quarterly versions
(eb06764098)
- [BLADE-743] cli: ConvertCommand: uses ReleaseUtil (2b4393066b)
- [BLADE-743] cli: BladeCLI: uses ReleaseUtil to get target platform version
(6573bd1438)
- [BLADE-743] cli: BladeCLI: refreshes releases if the refreshReleases flag is
found (9da0a8c85e)
- [BLADE-743] cli: adds flag to force Blade to check for new releases
(9e25ca5477)
- [BLADE-743] cli: adds ReleaseUtil (60ff1aad26)
- [BLADE-743] cli: adds ResourceUtil (b4d98fbfde)
- [BLADE-743] cli: bnd.bnd: includes json lib (c43add9065)
- [BLADE-743] cli: build.gradle, bnd.bnd: adds releases.json into the jar during
build (0c50c3361e)
- [BLADE-743] cli: build.gradle: adds idea plugin to generate IDE files
(02eadfcd8f)
- [BLADE-743] cli: build.gradle: bumps project templates version (c9fb7111e5)
- [BLADE-743] cli: build.gradle: updates json dependencies (77cc0e5ca0)

### Dependencies
- [BLADE-744] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.39.
- [BLADE-744] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.22.
- [BLADE-744] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.23.
- [BLADE-743] Update the com.liferay.project.templates dependency to version
5.0.289.
- [BLADE-743] Update the jackson-databind dependency to version 2.16.1.
- [BLADE-741] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.39-SNAPSHOT.
- [BLADE-741] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.6-SNAPSHOT.
- [BLADE-741] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.22-SNAPSHOT.
- [BLADE-741] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.23-SNAPSHOT.

## 5.0.1 - 2023-11-27

### Commits
- [BLADE-739] upgrade project template and change aether related libs name
(469a08ae6e)
- [BLADE-738] cli: LiferayDefaultVersionValidatorTest: updates assertion based
on latest product info (a394703a93)
- [BLADE-738] cli: removes WorkspaceProductComparator and
WorkspaceProductComparatorTest (9ebf5ac8a9)
- [BLADE-738] cli: BladeUtil: removes unused fields and methods (3de6c562fc)
- [BLADE-738] cli: BladeUtil: simplify entry usage (0a54e42514)
- [BLADE-738] cli: updates test resources to account for new quarterly keys
(17d275e8d0)
- [BLADE-738] cli: BladeUtil: uses ProductKeyUtil.comparator (f71505e277)
- [BLADE-738] cli: LiferayMoreVersionValidatorTest: uses
ProductKeyUtil.comparator (5ccaca532d)
- [BLADE-738] cli: LiferayMoreVersionValidator: uses new ProductKeyUtil
validators (89384a380e)
- [BLADE-738] cli: adds ProductKeyUtilTest to test the comparator (afbd31a9b0)
- [BLADE-738] cli: adds ProductKeyUtil to handle parsing and validation of
product key strings (1fca7b63bc)
- [BLADE-738] cli: adds ProductKeyInfo and ProductKeyVersion models to hold
comparisons (293d543534)
- [BLADE-738] add new regex patter for dxp-2023.q3.1 (c1036676d8)
- [BLADE-737] upgrade httpclient version to 5.2.1 (1b4ce44978)
- [BLADE-737] update httpClient dependency version (aa0df1341d)
- [BLADE-735] fix test error (b6ce6e27a5)
- [BLADE-730] remove upload test result (dc1fd80551)
- [BLADE-730] ignore convert tests (be4361da00)
- [BLADE-730] remove unused task dependency (45d7da88de)
- [BLADE-730] use same cache key in github action (c0a3a5cceb)
- [BLADE-730] improve task definition (730bf9d300)
- [BLADE-730] update junit to 4.13.1 for CVE-2020-15250 Vulnerability
(591ff727e5)
- [BLADE-730] update jcommand version for WS-2019-0490 Vulnerability
(9092a9c17d)
- [BLADE-730] make action can use cache (9661da3835)
- [BLADE-730] fix initBundle test error (2f14c91518)
- [BLADE-730] remove unnecessory powermock related dependencies (10377b8856)
- [BLADE-730] update junit for CVE-2020-15250 vulnerability (25141f8e47)
- [BLADE-730] update org.jsoup for CVE-2021-37714 vulnerability (e28ce8f478)
- [BLADE-730] update org.json for CVE-2022-45688 vulnerability (a7684ec2d8)
- [BLADE-730] update maven-aether-provider for CVE-2018-10237 vulnerability
(1dd350adc5)
- [BLADE-730] update common-text for CVE-2022-42889 vulnerability (1786fb6aba)
- [BLADE-730] update common-configuration2 for CVE-2022-33980 vulnerability
(90844eca1e)
- [BLADE-730] update common-compress for CVE-2019-12402 vulnerability
(fa97b3811d)
- [BLADE-730] update apache ant for CVE-2020-1945 vulnerability (d148801287)
- [BLADE-730] update common-io for CVE-2021-29425 vulnerability (835974b5a6)
- [BLADE-730] remove unnecessory dependency (8b1a74ac8d)
- [BLADE-730] improve task definition (7cbc52e6d9)
- [BLADE-730] remove check-report action (838c93f10a)
- [BLADE-730] improve performance (afa73ae88a)
- [BLADE-730] blade should support proxy when no userName and password"
(117c04697c)

### Dependencies
- [BLADE-740] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.38.
- [BLADE-740] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.5.
- [BLADE-740] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.21.
- [BLADE-740] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.22.
- [BLADE-739] Update the com.liferay.project.templates dependency to version
5.0.269.
- [BLADE-739] Update the maven-resolver-api dependency to version 1.9.17.
- [BLADE-739] Update the maven-resolver-connector-basic dependency to version
1.9.17.
- [BLADE-739] Update the maven-resolver-impl dependency to version 1.9.17.
- [BLADE-739] Update the maven-resolver-spi dependency to version 1.9.17.
- [BLADE-739] Update the maven-resolver-transport-classpath dependency to
version 1.9.17.
- [BLADE-739] Update the maven-resolver-transport-file dependency to version
1.9.17.
- [BLADE-739] Update the maven-resolver-transport-http dependency to version
1.9.17.
- [BLADE-739] Update the maven-resolver-transport-wagon dependency to version
1.9.17.
- [BLADE-739] Update the maven-resolver-util dependency to version 1.9.17.
- [BLADE-737] Update the httpclient5 dependency to version 5.2.1.
- [BLADE-737] Update the httpclient5-fluent dependency to version 5.2.1.
- [BLADE-737] Update the httpclient dependency to version 4.5.14.
- [BLADE-735] Update the com.liferay.project.templates dependency to version
5.0.264.
- [BLADE-730] Update the commons-io dependency to version 2.7.
- [BLADE-730] Update the ant dependency to version 1.10.11.
- [BLADE-730] Update the commons-compress dependency to version 1.21.
- [BLADE-730] Update the commons-configuration2 dependency to version 2.8.0.
- [BLADE-730] Update the commons-text dependency to version 1.10.0.
- [BLADE-730] Update the maven-resolver-provider dependency to version 3.6.3.
- [BLADE-730] Update the json dependency to version 20230227.
- [BLADE-730] Update the jsoup dependency to version 1.15.3.
- [BLADE-730] Update the junit dependency to version 4.13.1.
- [BLADE-730] Update the com.liferay.project.templates dependency to version
5.0.262.
- [BLADE-730] Update the maven-settings dependency to version 3.6.3.
- [BLADE-730] Update the maven-settings-builder dependency to version 3.6.3.
- [BLADE-729] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.38-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.5-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.21-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.22-SNAPSHOT.

## 5.0.0 - 2023-08-29

### Commits
- [BLADE-729] liferay-blade-cli: updates the remote repo host to use https
(7f1f55a91f)
- [BLADE-729] liferay-blade-cli: adds debug output to publish.sh (ee7317577c)
- [BLADE-729] liferay-blade-cli: rolls back loud diff code in publish.sh
(d8c427d56d)
- [BLADE-729] fix maven profile diff error (0a9fa3632d)
- [BLADE-729] liferay-blade-cli: copies the jar files to be zip files first
(88d7b0580c)
- [BLADE-729] liferay-blade-cli: explode the jars and do a recursive diff on the
directories (844e83ed0c)
- [BLADE-729] Revert "BLADE-729 liferay-blade-cli: diff loudly during publish
for now" (90c6c20e7a)
- [BLADE-729] liferay-blade-cli: diff loudly during publish for now (810de32285)
- [BLADE-729] liferay-blade-cli: allowInsecureProtocol for local nexus
publishing (638d497602)
- [BLADE-729] cli: manually roll back to 5.0.0 (94f6013962)
- [BLADE-729] disable macos verify (ea2fa5d7ef)
- [BLADE-729] restore remtoe deploy configuration (038995ce46)
- [BLADE-729] liferay-blade-cli: manually bumps snapshot version to 5.0.0
(a8547aec08)
- [BLADE-728] make test run in parallel model (3a9f04a80c)
- [BLADE-728] no need to private repo for gradle tooling test (954150e240)
- [BLADE-728] update http protocol to https (fc77264fb1)
- [BLADE-728] update github timeout for verify (806be464b7)
- [BLADE-728] update bnd.gradle to 5.3.0 (e028d246e6)
- [BLADE-728] improve build.gradle for gradle 7 (1926e0e1b8)
- [BLADE-728] upgrade blade configuraton for gradle 7 (aced46ae70)
- [BLADE-724] liferay-blade-cli: bumps bnd lib version to 5.3.0 in extensions
(d59e5a6ac6)
- [BLADE-728] improve build.gradle for gradle 7 (45604becfa)
- [BLADE-728] upgrade blade configuraton for gradle 7 (c17e7fe341)
- [BLADE-724] cli: bumps expected workspace version (1653063764)
- [BLADE-724] cli: for now, build against an exact release version of project
templates. Once we make the release process stable, we can explore making it
dynamic against the latest release. (af8f2e620c)
- [BLADE-724] fixes failure in GradleToolingTest (63cfb92dca)
- [BLADE-724] liferay-blade-cli: auto SF (b8ad762eff)
- [BLADE-724] liferay-blade-cli: bumps bnd gradle lib to 5.3.0 (f5daa56352)
- [BLADE-725] (152ef55dbf)
- [BLADE-725] add changelog command (a928cdfd7d)
- [BLADE-725] create release document (76835e08c6)
- [BLADE-724] liferay-blade-cli: always use the latest release of the project
templates artifact (9c84835665)
- [BLADE-719] update workspace plugin version for initBundle test (93e713adea)
- [BLADE-717] fix client extension test error (1d8fbc957e)
- [BLADE-717] Update Test (129b79cac3)
- [BLADE-717] Update samples url (0625cc4d4c)
- [BLADE-716] add test form simulator (a8b20a9bf9)
- [BLADE-716] set correct target platform for project template (3b6308f950)
- [BLADE-711] update github actions (42e893179e)
- [BLADE-711] Update blade server start error message (b9049836a7)
- [BLADE-712] improve blade samples command logic (6d762724f0)
- [BLADE-712] return local file path when uri start with file (fed725e8dc)
- [BLADE-712] add smoke test for client extenstion sample (c9c6baa160)
- [BLADE-712] improve download performance (96475c7816)
- [BLADE-712] check the file last modify date before download (e06e9e1738)
- [BLADE-710] install liferay js failed when path contain blank spaces
(b416b013f8)
- [BLADE-708] fix gradle tooling performance issue (61b98daa5d)
- [BLADE-709] fix client extension sample workspace renamed issue (64e0f4cd67)
- [BLADE-707] update sample minimal workspace name (a5cd5da27b)
- [BLADE-696] improve command description (7f5d33d133)
- [BLADE-696] remove hidden property for --list (ec405c9681)
- [BLADE-696] fix test error (df2e8b0919)
- [BLADE-696] add junit testAdd ability to download client extensions samples
from liferay-portal (a29eba3b0c)
- [BLADE-705] improve get properties (058f171a34)
- [BLADE-705] Fix NPE issue (961e738e35)
- [BLADE-705] Gradle local properties should also be loaded (2269e821a5)
- [BLADE-703] should return correct prodcut keys order (74a3071068)

### Dependencies
- [BLADE-729] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.37.
- [BLADE-729] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.4.
- [BLADE-729] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.20.
- [BLADE-729] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.21.
- [BLADE-729] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.37-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.4-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.20-SNAPSHOT.
- [BLADE-729] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.21-SNAPSHOT.
- [BLADE-729] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.36.
- [BLADE-729] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.3.
- [BLADE-729] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.19.
- [BLADE-729] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.20.
- [BLADE-729] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.35.
- [BLADE-729] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.2.
- [BLADE-729] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.18.
- [BLADE-729] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.19.
- [BLADE-728] Update the gradle-download-task dependency to version 5.4.0.
- [BLADE-728] Update the biz.aQute.bndlib dependency to version 5.3.0.
- [BLADE-728] Update the com.liferay.gogo.shell.client dependency to version
1.0.0.
- [BLADE-728] Update the com.liferay.project.templates dependency to version
5.0.257.
- [BLADE-728] Update the commons-beanutils dependency to version 1.9.4.
- [BLADE-728] Update the commons-io dependency to version 2.6.
- [BLADE-728] Update the commons-lang dependency to version 2.6.
- [BLADE-728] Update the ant dependency to version 1.10.7.
- [BLADE-728] Update the commons-compress dependency to version 1.18.
- [BLADE-728] Update the commons-configuration2 dependency to version 2.7.
- [BLADE-728] Update the commons-text dependency to version 1.8.
- [BLADE-728] Update the httpclient dependency to version 4.5.13.
- [BLADE-728] Update the httpcore dependency to version 4.4.14.
- [BLADE-728] Update the maven-aether-provider dependency to version 3.3.9.
- [BLADE-728] Update the maven-settings dependency to version 3.3.9.
- [BLADE-728] Update the maven-settings-builder dependency to version 3.3.9.
- [BLADE-728] Update the aether-api dependency to version 1.0.2.v20150114.
- [BLADE-728] Update the aether-connector-basic dependency to version
1.0.2.v20150114.
- [BLADE-728] Update the aether-impl dependency to version 1.0.2.v20150114.
- [BLADE-728] Update the aether-spi dependency to version 1.0.2.v20150114.
- [BLADE-728] Update the aether-transport-classpath dependency to version
1.0.2.v20150114.
- [BLADE-728] Update the aether-transport-file dependency to version
1.0.2.v20150114.
- [BLADE-728] Update the aether-transport-http dependency to version
1.0.2.v20150114.
- [BLADE-728] Update the aether-transport-wagon dependency to version
1.0.2.v20150114.
- [BLADE-728] Update the aether-util dependency to version 1.0.2.v20150114.
- [BLADE-728] Update the jansi dependency to version 1.17.1.
- [BLADE-728] Update the gradle-base-services-groovy dependency to version
5.6.4.
- [BLADE-728] Update the gradle-core dependency to version 5.6.4.
- [BLADE-728] Update the gradle-tooling-api dependency to version 5.6.4.
- [BLADE-728] Update the json dependency to version 20190722.
- [BLADE-728] Update the jsoup dependency to version 1.11.3.
- [BLADE-728] Update the xz dependency to version 1.6.
- [BLADE-728] Update the diffutils dependency to version 1.3.0.
- [BLADE-728] Update the junit dependency to version 4.12.
- [BLADE-728] Update the zipdiff dependency to version 1.0.
- [BLADE-728] Update the easymock dependency to version 3.5.1.
- [BLADE-728] Update the osgi.core dependency to version 6.0.0.
- [BLADE-728] Update the powermock-api-easymock dependency to version 2.0.4.
- [BLADE-728] Update the powermock-classloading-xstream dependency to version
2.0.4.
- [BLADE-728] Update the powermock-module-junit4 dependency to version 2.0.4.
- [BLADE-728] Update the powermock-module-junit4-rule dependency to version
2.0.4.
- [BLADE-728] Update the zt-process-killer dependency to version 1.9.
- [BLADE-724] Update the com.liferay.project.templates dependency to version
5.0.257.
- [BLADE-724] Update the biz.aQute.bnd.gradle dependency to version 5.3.0.
- [BLADE-724] Update the com.liferay.project.templates dependency to version
latest.release.
- [BLADE-719] Update the com.liferay.project.templates dependency to version
5.0.246.
- [BLADE-714] Update the com.liferay.project.templates dependency to version
5.0.243.
- [BLADE-713] Update the com.liferay.project.templates dependency to version
5.0.241.
- [BLADE-712] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.35-SNAPSHOT.
- [BLADE-712] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.2-SNAPSHOT.
- [BLADE-712] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.18-SNAPSHOT.
- [BLADE-712] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.19-SNAPSHOT.
- [BLADE-712] Update the commons-beanutils dependency to version 1.9.4.
- [BLADE-712] Update the ant dependency to version 1.10.7.
- [BLADE-712] Update the commons-configuration2 dependency to version 2.7.
- [BLADE-712] Update the commons-text dependency to version 1.8.
- [BLADE-712] Update the httpclient dependency to version 4.5.13.
- [BLADE-712] Update the httpcore dependency to version 4.4.14.
- [BLADE-712] Update the json dependency to version 20190722.
- [BLADE-712] Update the xz dependency to version 1.6.
- [BLADE-702] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.35-SNAPSHOT.
- [BLADE-702] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.2-SNAPSHOT.
- [BLADE-702] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.18-SNAPSHOT.
- [BLADE-702] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.19-SNAPSHOT.

## 4.1.1 - 2022-12-07

### Commits
- [BLADE-697] fix junit test error (0b16578f1e)
- [BLADE-700] parameterException message should be start with Error (371de49477)
- [BLADE-699] upgrade gradle download task version (7465dba63a)
- [BLADE-698] add test for commerce product (0ca492c121)
- [BLADE-698] fix regex error for commerce product (79086689f0)
- [BLADE-694] fix commerce product key support issue (e79fccc327)
- [BLADE-694] improve get product logic for convert service builder (18cc3cabfe)
- [BLADE-694] set correct liferay product for commerce (287b2c4353)
- [BLADE-694] fix variable name error (4368a4760d)
- [BLADE-694] restore compileOnly for maven-profile (dc72f85329)
- [BLADE-694] update project extension version (197d33e2a6)
- [BLADE-694] upgrade gradle version to 6.9.2 (3b5b30f8da)
- [BLADE-694] Rename parameter '--product' as '--liferay-product' (985c9b3d4d)
- [BLADE-695] add smoke test (eb7f416594)
- [BLADE-695] fix spelling error (aa8ecfd4b0)
- [BLADE-695] Fix bugs when parameter is mising (b43e3dffa7)
- [BLADE-692] fix error test (f4278907e1)
- [BLADE-692] set correct product base on target platform version (08a937171d)

### Dependencies
- [BLADE-701] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.34.
- [BLADE-701] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.1.
- [BLADE-701] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.17.
- [BLADE-701] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.18.
- [BLADE-697] Update the com.liferay.project.templates dependency to version
5.0.237.
- [BLADE-699] Update the gradle-download-task dependency to version 5.1.0.
- [BLADE-694] Update the com.liferay.project.templates dependency to version
5.0.235.
- [BLADE-692] Update the com.liferay.project.templates dependency to version
5.0.231.
- [BLADE-691] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.34-SNAPSHOT.
- [BLADE-691] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.1-SNAPSHOT.
- [BLADE-691] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.17-SNAPSHOT.
- [BLADE-691] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.18-SNAPSHOT.

## 4.1.0 - 2022-09-01

### Commits
- [BLADE-690] use version 5.0.224 project template (627b8c14e5)
- [LCD-14300] prevent error (9f3b333deb)
- [LCD-14300] remove debug (57b575b51f)
- [LCD-14300] debug (2491184f8d)
- [LCD-14300] debug (c6c17855b5)
- [LCD-14300] debug (2057554127)
- [LCD-14300] debug (aab13afa32)
- [LCD-14300] debug (344b1fafa8)
- [LCD-14300] switch to ubuntu-20 (2f6bcdeb7e)
- [LCD-14300] fix partial path traversal vulnerability (dbed8d96f0)
- [LCD-14300] make test less brittle (52be0fefc1)
- [LCD-14300] fetch CET api jar from BOM if available (f022d1278a)
- [LCD-14267] update to version 0.0.6 of lxc cli (9fdfcc1b99)
- [LCD-14267] fix caching (86e61d9bbe)
- [BLADE-688] add smoke test (366d24316b)
- [BLADE-688] use current dir to create client-extension (e8bc4c3b10)
- [BLADE-685] fix test error (260e8ee9e9)
- [BLADE-685] update to latest 5.0.213 project template (48d16506be)
- [LCD-14267] fix test (abe6fe31e5)
- [LCD-14267] pass EXTENSION_METADATA_FILE env var into lxc (4d9260b354)
- [LCD-14267] fix test (a770a8ceca)
- [LCD-14267] include dist/ file in ignore (b859f27ad5)
- [LCD-14267] include com.liferay.project.templates.client.extensions
(7850824791)
- [LCD-14267] -project-template-client-extension (74be35063e)
- [LCD-14267] add a new project-templates-client-extension (357376458b)
- [LCD-14295] update test version check (1917c51daa)
- [LCD-14267] add new hidden args that will be used for client-extension
template (448bac7d7e)
- [LCD-14267] disable windows test temporarily (ef67ff2214)
- [LCD-14267] fix initcommand test (314a427005)

### Dependencies
- [BLADE-690] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.33.
- [BLADE-690] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.0.
- [BLADE-690] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.16.
- [BLADE-690] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.17.
- [BLADE-690] Update the com.liferay.project.templates dependency to version
5.0.224.
- [BLADE-685] Update the com.liferay.project.templates dependency to version
5.0.223.
- [BLADE-687] Update the com.liferay.project.templates dependency to version
5.0.219.
- [BLADE-685] Update the com.liferay.project.templates dependency to version
5.0.213.
- [LCD-14267] Update the com.liferay.project.templates.client.extension
dependency to version 1.0.0-SNAPSHOT.
- [LCD-14295] Update the com.liferay.project.templates dependency to version
5.0.211.
- [LCD-14267] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.33-SNAPSHOT.
- [LCD-14267] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.16-SNAPSHOT.
- [LCD-14267] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.17-SNAPSHOT.

## 4.0.13 - 2022-05-18

### Commits
- [BLADE-635] (74f7cf9d17)
- [BLADE-668] simplify js-widget parameter (92e8e38bcc)
- [BLADE-668] add liferay cli to support create js project (64b2128ff5)
- [BLADE-635] Add validation for modules name (4833a9a2f0)
- [BLADE-677] Blade did not correctly verify product key in
LiferayMoreVersionValidator (8add80b301)
- [BLADE-674] fix init command test error (c444717692)
- [BLADE-674] (bb5c0ade5c)
- [BLADE-674] It's not possible to execute the option upgradeProperties with
blade (caf45f9e60)
- [BLADE-675] upgrade liferay-js-widget version (2294394d30)
- [BLADE-572] fix test failure (d1db6b9226)

### Dependencies
- [BLADE-678] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.32.
- [BLADE-678] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.15.
- [BLADE-678] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.16.
- [BLADE-678] Update the com.liferay.project.templates dependency to version
5.0.205.
- [BLADE-668] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.16-SNAPSHOT.
- [BLADE-673] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.32-SNAPSHOT.
- [BLADE-673] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.15-SNAPSHOT.
- [BLADE-673] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.15-SNAPSHOT.
- [BLADE-673] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.31.
- [BLADE-673] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.14.
- [BLADE-673] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.14.
- [BLADE-572] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.14-SNAPSHOT.
- [BLADE-572] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.14-SNAPSHOT.

## 4.0.12 - 2022-03-26

### Commits
- [BLADE-672] fix test failure (b8a0fe9867)
- [BLADE-672] (6a5cb179a1)
- [BLADE-664] Update project templates (894c346aa0)

### Dependencies
- [BLADE-673] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.31.
- [BLADE-673] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.14.
- [BLADE-673] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.14.
- [BLADE-672] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.14-SNAPSHOT.
- [BLADE-672] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.14-SNAPSHOT.
- [BLADE-673] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.31-SNAPSHOT.
- [BLADE-673] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.13-SNAPSHOT.
- [BLADE-673] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.13-SNAPSHOT.
- [BLADE-664] Update the com.liferay.project.templates dependency to version
5.0.189.

## 4.0.11 - 2022-03-25

### Commits
- [BLADE-664] -1 Add validation for creating war-core-ext project (2583ddae2f)
- [BLADE-664] Add validation for creating war-core-ext project on liferay 73 and
greater (954c88ee80)
- [BLADE-532] Sort commands for help (ca69ed538e)
- [BLADE-664] Add validation for creating war-core-ext project on liferay 73 and
greater (273843ade6)
- [BLADE-644] use same version node in nodeUtil (4fcb55708b)
- [BLADE-644] Theme is not displayed on the portal 7.4 webpage (cf6223f102)

### Dependencies
- [BLADE-673] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.30.
- [BLADE-673] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.12.
- [BLADE-673] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.12.
- [BLADE-664] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.30-SNAPSHOT.
- [BLADE-664] Update the com.liferay.project.templates dependency to version
5.0.179.
- [BLADE-644] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.12-SNAPSHOT.
- [BLADE-644] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.12-SNAPSHOT.
- [BLADE-644] Update the com.liferay.project.templates dependency to version
5.0.177.
- [BLADE-663] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.29-SNAPSHOT.
- [BLADE-663] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.11-SNAPSHOT.
- [BLADE-663] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.11-SNAPSHOT.

## 4.0.10 - 2021-11-19

### Commits
- [BLADE-632] Add support to convert to liferay 7.4 (87663eaeb3)
- [BLADE-657] update tests (148a1f40e6)
- [BLADE-657] Fix compatibility problem on the coming 7.4 update 1 version
(caffefca36)
- [BLADE-646] fix release api setting error (8e9a14fef0)
- [BLADE-646] improve create project and release api logic (ba6e78df93)
- [BLADE-646] fix convert command issue (72c28c799d)
- [BLADE-659] upgrade tempalte and template.extesion (82febf7ff0)
- [BLADE-508] quote replacement for file seperator in replaceAll method
(35adc8d152)
- [BLADE-646] Blade convert should modify all the release api dependencies when
migrating 62 projects (370d6fe61f)
- [LPS-141989] fix tests (d8c6a82e80)
- [LPS-141989] update to latest project templates (bc9da0966f)
- [BLADE-655] Need to add validation to the creation of rest-builder 70
(061b84a1c2)
- [BLADE-649] Fix npe error when creating manve module (019f187edb)
- [BLADE-651] get default product version dynamically (479f3b4047)
- [BLADE-643] update project templates to latest 5.0.157 (e60ba1f1ce)
- [BLADE-643] update node version to 10.15.3 to fix js-theme smoke tests
(e4ac0c7566)
- [BLADE-643] fix tests (5a7ce4c183)
- [LPS-137135] update to latest project templates (55c1cacccb)
- [BLADE-640] Add test (c72b55610c)
- [BLADE-640] Remove config.json when after project is created (68bc7e8cf9)

### Dependencies
- [BLADE-660] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.28.
- [BLADE-660] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.10.
- [BLADE-660] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.10.
- [BLADE-657] Update the com.liferay.project.templates dependency to version
5.0.165.
- [BLADE-659] Update the com.liferay.project.templates dependency to version
5.0.163.
- [] Update the com.liferay.project.templates dependency to version 5.0.161.
- [LPS-141989] Update the com.liferay.project.templates dependency to version
5.0.161.
- [BLADE-649] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.28-SNAPSHOT.
- [BLADE-643] Update the com.liferay.project.templates dependency to version
5.0.157.
- [LPS-137135] Update the com.liferay.project.templates dependency to version
5.0.155.
- [BLADE-639] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.27-SNAPSHOT.
- [BLADE-639] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.10-SNAPSHOT.
- [BLADE-639] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.10-SNAPSHOT.

## 4.0.9 - 2021-07-01

### Commits
- [LPS-134964] Explicitly list workspace properties (810f39639f)
- [BLADE-629] Add test (d455ece444)
- [BLADE-629] Point users to blade 3.9.2 for older Product Keys (01cdb8dd0f)
- [LPS-134232] Add test cases to catch inadvertent defaults (033666de66)
- [BLADE-637] Simplify liferayVersion in JS Theme template (4675777dbd)
- [BLADE-637] Gradle moved to jfrog? (0a86de79f6)
- [BLADE-637] Fix test (c96a47f76f)
- [BLADE-637] Add micro version when calculating Liferay version (2667029721)
- [BLADE-637] Consistent variable naming (3550241957)
- [BLADE-637] Revert "BLADE-637 Add more version constants for tests"
(aa14a5cd4c)
- [BLADE-637] Use updated VersionUtil (45ae432fef)
- [BLADE-637] Use project-templates-extensions VersionUtil (0efa037c07)
- [BLADE-637] Add more version constants for tests (5965f53c47)
- [BLADE-637] Use VersionUtil class to format version (c2c97198e4)
- [BLADE-637] Update project templates to remove Version usage (101ba247cf)
- [BLADE-637] Add test case (4fe1cdd17c)
- [BLADE-637] Give prompter BOM versions for maven init (5d1dc2b35b)
- [BLADE-637] Update to 7.4.1 patched BOM version (97b88a0a36)
- [BLADE-637] Allow hyphens in the revision parameter (daef9c1bb2)
- [LPS-133987] Update workspace version (9575111bb8)
- [LPS-133987] Update ant-bnd in workspace (e78c451db8)
- [LPS-133530] Add test case to check generated DTDs (4a54de6f73)
- [LPS-133530] Fix test case (82360aa4bf)
- [LPS-133530] Update maven tests to be more explicit with version (db8e03790d)
- [LPS-133530] Use more complex version matching (27b7bee50e)
- [LPS-133530] Revert version list (7b1f96f107)
- [LPS-133530] Fix sample maven test case (b923a670f9)
- [LPS-133530] Set liferayVersion explicitly in maven builds (ec0a5b9d8a)
- [LPS-133530] Set liferayVersion manually for create command in maven
(cb20b8fe06)
- [LPS-133530] Add latest constants (ae4735814f)
- [LPS-133530] Update to latest workspace version (4e28fd5684)
- [LPS-133530] Add new versions of Liferay Portal to tests (7ef9671eb7)
- [LPS-133530] Don't set product version automatically (94603f688b)
- [LPS-133530] Add Maven DXP test cases (5c05bf7702)
- [LPS-133530] Update to correct dependency for DXP (0b8dfde082)
- [LPS-133530] Use less strict versioning for Project Templates (27a7843bac)
- [LPS-133530] add code to avoid SF removing (0ae09d94b4)
- [LPS-133530] Test revert (2c673c1934)
- [LPS-133530] Fix remaining issues (36a64c5666)
- [LPS-133530] Temporarily ignore SF checks until bugs are fixed (a63269359c)
- [LPS-133530] Temporarily ignore these SF checks (d44004c1d1)
- [LPS-131920] update default 7.3 product key to portal-7.3-ga7 (7ccbad6aaa)
- [LPS-131071] Update form-field template for 7.4 compatibility (0dc71a23dc)
- [LPS-130908] Update workspace version for 7.4 (f0cc02076e)
- [LPS-130908] Fix spring-mvc template (71848a92ec)
- [BLADE-633] Removed 7.4 DXP EP1 from promoted list (0d0fd8882a)

### Dependencies
- [BLADE-639] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.26.
- [BLADE-639] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.9.
- [BLADE-639] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.9.
- [LPS-134964] Update the com.liferay.project.templates dependency to version
5.0.153.
- [LPS-134232] Update the com.liferay.project.templates dependency to version
5.0.151.
- [BLADE-637] Update the com.liferay.project.templates dependency to version
5.0.149.
- [BLADE-637] Update the com.liferay.project.templates dependency to version
5.0.148.
- [LPS-133987] Update the com.liferay.project.templates dependency to version
5.0.146.
- [LPS-133987] Update the com.liferay.project.templates dependency to version
5.0.144.
- [LPS-133530] Update the com.liferay.project.templates dependency to version
5.0.140.
- [LPS-131071] Update the com.liferay.project.templates dependency to version
5.0.131.
- [LPS-130908] Update the com.liferay.project.templates dependency to version
5.0.129.
- [BLADE-633] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.26-SNAPSHOT.
- [BLADE-633] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.9-SNAPSHOT.
- [BLADE-633] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.9-SNAPSHOT.

## 4.0.8 - 2021-04-29

### Commits
- [BLADE-631] fix tests (0a23d70b54)
- [BLADE-631] support init a liferay 7.4 workspace and make it default
(9d5f70f802)
- [LPS-130802] Add compatibility for 7.4 projects (09ccb33b2b)
- [LPS-130023] Newer 7.3 DXP available (e7ff28635c)
- [LPS-130023] Fix remaining service builder tests (efb06712dd)
- [LPS-130023] Improve logic (dae70ec187)
- [LPS-130023] Fix test (384370873e)
- [LPS-130023] Update workspace version (414a0713b9)
- [LPS-130023] Add 7.4 compatibility for project creation (ca96f68e0e)
- [BLADE-630] Test fix (4b086d8b0c)
- [BLADE-630] Consistent whitespace for key value pair (d6997a9d18)
- [BLADE-630] No longer use liferay.version.default (4caf402f7c)
- [BLADE-628] Prep Next (81e87f6edb)

### Dependencies
- [BLADE-633] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.25.
- [BLADE-633] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.8.
- [BLADE-633] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.8.
- [LPS-130802] Update the com.liferay.project.templates dependency to version
5.0.127.
- [LPS-130023] Update the com.liferay.project.templates dependency to version
5.0.123.
- [BLADE-628] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.25-SNAPSHOT.
- [BLADE-628] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.8-SNAPSHOT.
- [BLADE-628] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.8-SNAPSHOT.

## 4.0.7 - 2021-03-09

### Commits
- [LPS-128040] Update rest builder template for jdk 11 support (308d97f6f0)
- [BLADE-627] Disable Gradle module metadata publication (0ce2e9aa32)
- [IDE-4932] Update gradle-plugins-theme-builder with correct
gradle-plugins-css-builder dependency for gradle 5.6.4 compatibility
(3dccf1fae4)
- [BLADE-622] Update expected workspace version (b53d6cd6bb)
- [BLADE-622] Update project templates with gradle 6.6.1 (e2fc0488f4)
- [BLADE-622] Include project templates with service builder to compile with
java 8 instead of 11 (89fabad0b2)
- [BLADE-622] update workspace plugin with liferayWorkspace.docker* fix
(b8be68e6c1)
- [BLADE-622] Update workspace plugin for gradle 6 (fe49e6eba7)
- [BLADE-622] Build Scans have been moved to Gradle enterprise plugin
(81bba0c093)
- [BLADE-622] Update gradle to 6.6.1 (f7399c4598)
- [BLADE-621] provide a default value for version with init command (3309cb1a1b)
- [BLADE-621] update smoke tests (86b5ed2e0e)
- [BLADE-621] move -l --list --all options to original InitCommand (b473f173df)
- [BLADE-620] Properly handle relative paths as input (f9d1456227)

### Dependencies
- [BLADE-628] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.24.
- [BLADE-628] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.7.
- [BLADE-628] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.7.
- [LPS-128040] Update the com.liferay.project.templates dependency to version
5.0.113.
- [IDE-4932] Update the com.liferay.project.templates dependency to version
5.0.111.
- [BLADE-622] Update the com.liferay.project.templates dependency to version
5.0.109.
- [BLADE-622] Update the com.liferay.project.templates dependency to version
5.0.107.
- [LPS-125495] Update the com.liferay.project.templates dependency to version
5.0.105.
- [BLADE-618] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.24-SNAPSHOT.
- [BLADE-618] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.7-SNAPSHOT.
- [BLADE-618] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.7-SNAPSHOT.

## 4.0.6 - 2020-12-22

### Commits
- [BLADE-617] Update workspace constant (af1ca4ecd2)
- [BLADE-611] use try/with resources and inline variables (35093f25b7)
- [BLADE-611] use constant expression (6723c11256)
- [BLADE-611] combine filter statements (e6b39d9247)
- [BLADE-611] improve logic (8c90bb2ff8)
- [BLADE-611] remove outdated temp directory when start blade (4e4ba7ea57)
- [LPS-122967] Update local install script as well (353a249b5e)
- [LPS-122967] Don't use multiline (7d809ed917)
- [LPS-122967] Fix typos in script (35a2fc5ab7)
- [LPS-122967] Update tests with new war changes (000f495aa1)
- [LPS-122967] Moved war configuration into workspace plugin (a777da8eac)
- [LPS-122967] Use simplified workspace template (d05c9340ec)
- [LPS-106787] Add Test (ad75d3add5)
- [LPS-106787] Enable options for add ons (f0e0c2b178)
- [LPS-106787] Add support for UAD Options in service builder template
(7e1d875ce8)
- [LPS-106787] download and install latest blade.jar (f8df84c04c)

### Dependencies
- [BLADE-618] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.23.
- [BLADE-618] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.6.
- [BLADE-618] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.6.
- [BLADE-617] Update the com.liferay.project.templates dependency to version
5.0.103.
- [] Update the commons-lang dependency to version 2.6.
- [LPS-122967] Update the com.liferay.project.templates dependency to version
5.0.101.
- [LPS-106787] Update the com.liferay.project.templates dependency to version
5.0.97.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.23-SNAPSHOT.
- [] Update the com.liferay.project.templates.js.theme dependency to version
1.0.6-SNAPSHOT.
- [] Update the com.liferay.project.templates.js.widget dependency to version
1.0.6-SNAPSHOT.

## 4.0.5 - 2020-10-07

### Commits
- [BLADE-607] Print more in help message (b8797757d4)
- [BLADE-604] Update tests (f92ebd048e)
- [BLADE-604] inline (f9ee7f8d91)
- [BLADE-604] Update project templates to fix the path issues (8119794486)
- [BLADE-604] Fix path issues (54da7c2edb)

### Dependencies
- [BLADE-605] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.22.
- [BLADE-605] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.5.
- [BLADE-605] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.5.
- [BLADE-604] Update the com.liferay.project.templates dependency to version
5.0.93.
- [BLADE-603] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.22-SNAPSHOT.
- [BLADE-603] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.5-SNAPSHOT.
- [BLADE-603] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.5-SNAPSHOT.

## 4.0.4 - 2020-09-22

### Commits
- [BLADE-599] handle single digit dates (c5c457600f)
- [BLADE-599] sort qualifiers by release date (d9f1c11bb4)
- [BLADE-597] Add test cases (42e113d009)
- [BLADE-597] Only support "portal" for product value in maven (cc37775173)
- [BLADE-597] don't truncate the liferay version (6d8ea1ae14)
- [BLADE-597] reduce chances of NPE (55d020be39)
- [BLADE-597] Simplify logic (9666ba500d)
- [BLADE-597] Add product to MavenWorkspaceProvider (9bc59a7d1f)
- [BLADE-597] Try to get product value from Target platform (b8b7681d4c)
- [BLADE-598] Add product to Init command (c673aaf9d7)
- [BLADE-597] Fix tests (eebe041eca)
- [BLADE-597] Get product value from properties (5b8385049c)
- [BLADE-597] Look up product value in gradle.properties (ceac14adbe)
- [BLADE-598] Add product parameter option for ConvertCommand (73a3be0f55)
- [LPS-120852] switch to compile configuration (fb96931a5b)
- [LPS-120852] Add 7.2 test case (1e2cd8b946)
- [LPS-120852] Does not support jdk 11 (490a91f944)
- [LPS-120852] Use GA5 in testing (690fe278ba)
- [LPS-120852] Add tests to verify resolve task in workspace (502d1806d7)
- [BLADE-596] make test stable on java11 (8dfb4f4daa)
- [BLADE-596] simplify (ba238acd62)
- [BLADE-596] rename (4febdba74b)
- [BLADE-596] Fix propertiesLocator tests (1c34fcc4fd)
- [BLADE-596] add field back for tests (7451bd3b2b)
- [BLADE-596] Refactor the output and the code to make them simplier
(156119df6d)
- [BLADE-596] Refactor, we do not need to re-evaluate exceptions (989b3210be)
- [BLADE-596] Normalize the properties to do not load wrong lines as properties
(09fbd71c85)
- [BLADE-596] Upgrade Processes properties are not longer needed (7ec1ad3451)
- [LPS-120852] Add resolve task to workspace projects (632855874a)
- [BLADE-593] empty string is the same as no value (80771d84fc)
- [BLADE-593] Use Optional<String> (c33cd727f1)
- [BLADE-593] Rename method (abb0d20405)
- [BLADE-593] Use Optional<String> (8a6b87294f)
- [BLADE-593] Don't prompt for Liferay Version (7ad7b4b426)
- [BLADE-593] Update Test (8ec2c9a2fd)
- [BLADE-593] Refactor (2d44491f88)
- [BLADE-539] Print more helpful message (85e565eb0b)
- [BLADE-593] Fix test (7acd94f416)
- [BLADE-593] Out of scope for this pull (7bc0be51d9)
- [BLADE-539] Fix Test (3c6869bba1)
- [BLADE-593] Get product value from Workspace Provider (1bb9399829)
- [BLADE-593] Add Create Tests from docker property (07862548c3)
- [BLADE-593] Use constants when possible (60290b97ba)
- [BLADE-593] Add tests (83d5e0fabd)
- [BLADE-593] Capture dxp and portal images (bb367ca9af)
- [BLADE-539] Refactor to WorkspaceProvider (1be2aa28ca)
- [BLADE-539] Always take user input over workspace setting (11874fe6a3)
- [BLADE-593] : create throws NPE from within a dxpcloud provisioned Liferay
workspace. Fix NPE, prompt to ask user for selecting the liferay version if
liferay.workspace.product is missing, and add test case. (f0af778600)
- [LPS-120734] Set product argument (915fbd5144)
- [LPS-120734] Add product parameter option (cdf70b8d79)
- [LPS-120734] Add DXP Default Test (a439d229a6)
- [LPS-120734] Update test (03132df224)
- [LPS-120734] Only use release.portal.api >= 7.3 (e7d0ab3602)

### Dependencies
- [BLADE-600] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.21.
- [BLADE-600] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.4.
- [BLADE-600] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.4.
- [LPS-120852] Update the com.liferay.project.templates dependency to version
5.0.89.
- [LPS-120734] Update the com.liferay.project.templates dependency to version
5.0.87.
- [BLADE-595] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.21-SNAPSHOT.
- [BLADE-595] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.4-SNAPSHOT.
- [BLADE-595] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.4-SNAPSHOT.

## 4.0.3 - 2020-09-03

### Commits
- [BLADE-594] Temporarily ignore test (28a92d24b4)
- [BLADE-594] Fix verify scripts as well (a946f49108)
- [BLADE-594] move earlier (467276deaa)
- [BLADE-594] Add timeout for verify job (834fa6d732)
- [BLADE-594] fix permissions (1e45cd6edd)
- [BLADE-594] remove debug (09712373b0)
- [BLADE-594] simplify (55feb88e87)
- [BLADE-594] Switch to -bin for publish (f8f31b9cd3)
- [LPS-120193] Add Liferay Nexus to workspace pom (90a789433d)
- [LPS-119853] Add Extension args to CreateArgs (d9a266733a)
- [LPS-119853] Add 73 Form Field React Test (aaafadb68e)
- [LPS-119853] Add 7.3 React option for Form Field template (dd68869956)
- [BLADE-592] Update Tests (6018647ab4)
- [BLADE-592] Use release.portal.api in blade (961bb56ac4)

### Dependencies
- [BLADE-595] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.20.
- [BLADE-595] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.3.
- [BLADE-595] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.3.
- [LPS-120193] Update the com.liferay.project.templates dependency to version
5.0.85.
- [LPS-119853] Update the com.liferay.project.templates dependency to version
5.0.81.
- [BLADE-592] Update the com.liferay.project.templates dependency to version
5.0.79.
- [BLADE-591] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.20-SNAPSHOT.
- [BLADE-591] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.3-SNAPSHOT.
- [BLADE-591] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.3-SNAPSHOT.

## 4.0.2 - 2020-08-21

### Commits
- [LPS-105873] Check file output instead of buildTask outcome (d017cd6202)
- [LPS-105873] More robust (a4f5d32448)
- [LPS-105873] Only need major.minor versions for CreateCommand (516498bbdd)
- [LPS-105873] Updated promoted list (06a8a9e450)
- [LPS-105873] rc8 (0fad3d44c7)
- [LPS-105873] Rename confusing test (5d2f4a174d)
- [LPS-105873] Use fixed version of Project Templates (cedab4aec4)
- [LPS-105873] GA5 is now promoted (06a814d89a)
- [LPS-105873] Add Liferay Nexus Repo to tests (0ed324b556)
- [LPS-105873] fix SF (fb958c7dab)
- [LPS-105873] Update Test (3d02b98834)
- [LPS-105873] Add project templates change that supports workspace with yarn
support (6a588c4d97)
- [BLADE-589] cache the product_info map (5879c95f71)
- [BLADE-589] fix error message (37ff8da63a)
- [BLADE-589] fix tests (f6861c5ebc)
- [BLADE-589] Remove blade default product key list (6e6526cd9f)
- [BLADE-563] fix test (48caef6844)
- [BLADE-563] download product_info.json instead of directly embedding
(607e375843)
- [BLADE-563] make safer (be47f8fad3)
- [BLADE-563] print errors is using --trace for getting product_info
(ec9bc4e52a)
- [BLADE-563] sort (afc28349b5)
- [BLADE-563] Blade init -v should work without internet (7c005c8e49)
- [BLADE-588] remove possible nulls (c94737a570)

### Dependencies
- [BLADE-591] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.19.
- [BLADE-591] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.2.
- [BLADE-591] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.2.
- [LPS-105873] Update the com.liferay.project.templates dependency to version
5.0.77.
- [LPS-105873] Update the com.liferay.project.templates dependency to version
5.0.75.
- [BLADE-587] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.19-SNAPSHOT.
- [BLADE-587] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.2-SNAPSHOT.
- [BLADE-587] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.2-SNAPSHOT.

## 4.0.1 - 2020-07-29

### Commits
- [BLADE-580] use fixed jpm (77700befb5)
- [BLADE-582] improve printout (6a2e1b09c2)
- [BLADE-582] delete test (eaf1eccecb)
- [BLADE-582] make methods private and better null handling (17b0428509)
- [BLADE-582] wordsmith (d6a77f9bdf)
- [BLADE-576] switch to using optional in case of missing remote resources
(af8e90b0e8)
- [BLADE-585] make target platform version compatible (6c7189f010)
- [BLADE-579] make sure and create themes dir (3d495e2f4b)
- [BLADE-579] better handle quiet mode in convert command (5043feede6)
- [BLADE-578] show create command only in a workspace (f498ccd074)
- [BLADE-577] hide commands with CommandType.HIDDEN (85832a2ba3)
- [BLADE-576] Revert "Merge pull request #1111 from gamerson/BLADE-576-quick"
(4b13f792ab)
- [BLADE-576] quick ignore to get a new snapshot published (62eb0c403c)
- [BLADE-567] Add Liferay version possible value support when init maven
workspace (b0958f4fc3)
- [BLADE-570] touch up (0fdec2e670)
- [BLADE-570] Fix test parameters (d247598f47)
- [BLADE-570] Add Init More Test (141afd8a15)

### Dependencies
- [BLADE-587] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.18.
- [BLADE-587] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.1.
- [BLADE-587] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.1.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.18-SNAPSHOT.
- [] Update the com.liferay.project.templates.js.theme dependency to version
1.0.1-SNAPSHOT.
- [] Update the com.liferay.project.templates.js.widget dependency to version
1.0.1-SNAPSHOT.
- [] Update the com.liferay.project.templates.js.theme dependency to version
1.0.0.
- [] Update the com.liferay.project.templates.js.widget dependency to version
1.0.0.

## 4.0.0 - 2020-07-08

### Commits
- [BLADE-570] fix the "more" option on init command (92833aede5)
- [BLADE-519] add support for removing and migrating spring dependencies
(881021d2ae)
- [BLADE-518] move back to original package. (16ba3ab688)
- [BLADE-518] remove unused dependency for convert command (7b009e05af)
- [BLADE-568] switch to https (8c90a9ee48)
- [BLADE-568] check for both redirect codes (e856a2a128)
- [BLADE-564] update test (02a8b27fd8)
- [BLADE-564] rename and use rootProject.files (c4b9b8137a)
- [BLADE-564] use optional and lambda (f22aea9b71)
- [BLADE-564] rename (f98db4bc87)
- [BLADE-564] blade convert should convert docroot/WEB-INF/lib/*.jar
dependencies (8484ff870e)
- [BLADE-517] add test for spring portlet (281fd32e0f)
- [BLADE-517] use standard List api. (43036cbe34)
- [BLADE-517] add test for portal classpath depenedencies (314b86ffa0)
- [BLADE-517] make field final and rename (05ca13a4df)
- [BLADE-517] use a static final map with a static field initializer section
(6696cded80)
- [BLADE-517] should add all portlet plugin API jars to build.gradle
dependencies (df9fb13540)
- [BLADE-522] update test (57a88263b6)
- [BLADE-522] prefer mavenCentral (974154c116)
- [BLADE-522] fix arg class (b745c50a48)
- [BLADE-522] remove ignore and then add new init test (db32afa0d7)
- [BLADE-522] add better checking for workspace directory (9a793eb330)
- [BLADE-522] resolve the failures of tests (e270f9a90e)
- [BLADE-552] Check that blade create points to a path inside Liferay Workspace
(6b17e2d35b)
- [BLADE-565] use project template build to better initialize war project
(e05b340c7d)
- [BLADE-566] Update template count (e679a925f8)
- [BLADE-566] Integrate new project templates with REST builder (35c7c23bab)
- [BLADE-548] always remove WEB-INF/classes folder if exists (4356e92a8e)
- [BLADE-548] also delete generated xml files (79c853daa3)
- [BLADE-548] delete pre-existing service.xml (3c991a1959)
- [BLADE-548] don't move but copy instead (17d7577dc9)
- [BLADE-548] null check (be7180cdd8)
- [BLADE-548] improve service builder conversion (b7071b277e)
- [LPS-114909] Only remove comments from actual XML element (2c96f2c4b3)
- [LPS-114909] Add activation element in comments because Maven model removes
redundant elements (3de840e510)
- [BLADE-538] Use base dir to find gradle properties (7bb59826d3)
- [BLADE-538] Default to modulesDir (44d18e38fd)
- [BLADE-538] Add test case for null modulesDir (eeade0165b)
- [BLADE-538] Test using modules instead of wars (42cbcb72eb)
- [BLADE-538] Use templates with liferay.workspace.product (2130bc5a1b)
- [LPS-111461] Update tests to check in base workspace dir for default projects
(0f1d3adff8)
- [LPS-111461] Rename (145f6a2e81)
- [LPS-111461] Fix Convert Tests (c7a95aa2fd)
- [LPS-111461] Support liferay.workspace.wars.dir if set, otherwise use default
modules dir (5cc515214d)
- [LPS-111461] Issue with sort command not properly sorting create args first
(a4111f4f40)
- [LPS-111461] Update test cases for new behavior (5a5dcd60e9)
- [LPS-111461] Create command should execute in current dir unless specified
(7a8076c2b4)
- [LPS-111461] Support liferay.workspace.wars.dir if already set (cb09aa80af)
- [LPS-111461] Create run report after tests are archived (6d40bf75ac)
- [LPS-111461] Fix more tests (6ece0784b4)
- [LPS-111461] Update Test (2e9ccfbe8e)
- [LPS-111461] Use templates that remove warsDir in workspace (235fc7a9f8)
- [BLADE-533] fix sorting for promoted products (03cdaeeb44)
- [BLADE-533] now that we have promoted field, use it (ed8bb0cec4)
- [BLADE-553] update to fixed version (2d72e8d7e7)
- [BLADE-533] switch to non-cdn and add tests (9d92bb9e55)
- [BLADE-533] Use non-cdn url (fb4463460c)
- [BLADE-553] rename and add more argument possibilities (dcb274ade8)
- [BLADE-553] add init -l and init -a -l command to show possible product
(10a39133fd)
- [BLADE-553] -fix-sort (2753f53faa)
- [BLADE-515] improve the coverage of portal-dependency-jars-62.properties
(2886b8d208)
- [BLADE-533] switch to CDN url (dbb42d72a4)
- [BLADE-533] remove deprecated api calls (61c74342cc)
- [BLADE-533] fix duplicate packages (08cf75cb89)
- [BLADE-533] smoke tests reset streams before asserts (35050a8823)
- [BLADE-533] add tests (4335321b26)
- [BLADE-533] improve default and more version possible versions (3678981659)
- [BLADE-533] Use List instead of Collection (7a0419c47c)
- [LPS-111461] Smoke Test fixes (40dff4749b)
- [LPS-114909] Fix maven standalone test (be617272b2)
- [LPS-114169] fix tests (5ce0ba2fe3)
- [LPS-114169] Add liferay.workspace.product to template LPS-114909 Match target
platform BOM version inside workspace for maven projects (6385e09f6b)
- [BLADE-553] fix sample test (8cb17e2a10)
- [BLADE-553] fix maven init test (1eca1de353)
- [BLADE-553] add http client package in bnd.bnd (19317ed6c6)
- [BLADE-553] update logic for -d and --base options and fix tests (a173eac8b2)
- [BLADE-553] update smoke tests (66e5d2a3e9)
- [BLADE-553] fix smoke test (398208e0ea)
- [BLADE-553] update messages (6f9ec628f1)
- [BLADE-553] make new API have defaults to avoid breaking maven profile
(0439c041fd)
- [BLADE-553] add smoke test to make sure legacy '7.2' versions work
(3496d210b0)
- [BLADE-553] don't glob packages (921b727db7)
- [BLADE-553] fix smoke test error (22f350b3e2)
- [BLADE-553] get liferay version from workspace product info (b4b53f9967)
- [BLADE-553] ignore liferay default version test in .blade.properties
(6ce71dc044)
- [BLADE-533] support type more to get entire possbile values (248d466c12)
- [BLADE-553] Blade init should prompt user for all possible product versions
(b724476569)
- [BLADE-558] check run reporter not supported on windows/macos (4a9a8bf75f)
- [BLADE-558] fix report paths (73bc1106eb)
- [BLADE-558] give test uploads different names (a3cdbb605b)
- [BLADE-558] add check-run-reporter github action (981da129aa)
- [BLADE-558] fix error code (d57f22315d)
- [BLADE-558] convert command should list the new paths that have been converted
(2dba49b95f)
- [BLADE-557] convert list option should support -q option (fafde474c9)
- [BLADE-554] Wrong directories (e31fc3d0eb)
- [BLADE-554] Remove comments in pom file (1cc0ebd091)
- [BLADE-554] Create maven projects in workspace (cd190e54ee)
- [BLADE-554] Update workspace version (e251fa0702)
- [BLADE-554] Fix duplicate target platform versions in maven workspaces
(0f1033a26f)
- [BLADE-555] getBase returns a File now (5fcf922c31)
- [BLADE-555] these commands are workspace only (48687abf0a)
- [BLADE-555] minor changes (73655ff411)
- [BLADE-555] hide certain create command options (1d2c1f7c78)
- [BLADE-555] revert profileName change (0204746231)
- [BLADE-555] update maven profile with getBase/getProfileName changes
(1cc2a23095)
- [BLADE-555] set program name (7f6af47165)
- [BLADE-555] consistent usage messages (7d3f7f9807)
- [BLADE-555] show the more appropriate help message depending on your cwd
(553f2e52f6)
- [BLADE-555] getBase should return a file instead of a string because that's
what everyone wants (30d13dcd5d)
- [BLADE-555] hide dev options (3ea7511477)
- [BLADE-555] profile arg isn't needed by every command (f9ba952f9c)
- [LPS-114088] not needed (2feac60ef2)
- [LPS-114088] 5.0.43 has the actual form field fix (cabf56b5d6)
- [LPS-114088] Fix workspace path (e1271b1f4b)
- [LPS-114088] Check blade properties (a76c300bed)
- [LPS-114088] Check test in CI (08ff87603a)
- [LPS-114088] Fix form field template when using custom package (30d38a4b13)
- [BLADE-547] add back missing classes (917b070a3c)
- [BLADE-550] ignore maven profile tests on windows (2bc0731d08)
- [BLADE-550] Deprecated 6 templates from project templates main (8db18c1d54)
- [BLADE-550] Update tests (08ea35607b)
- [BLADE-550] Not needed (47b93b5887)
- [BLADE-550] Publish snapshots first (755b7aa9e3)
- [BLADE-550] Integrate Project templates with deprecated templates (49f8a11a1c)
- [BLADE-550] Duplicate? (95e309c6ce)
- [BLADE-550] Publish extensions only if version changes (db7115ec43)
- [BLADE-550] Use version instead of template token (9c2ae9ea74)
- [BLADE-550] Move deprecated project template archetypes to extensions
(d141115332)
- [LPS-112511] Add npm angular test to github actions (9de10f91df)
- [BLADE-545] add env to github action instead (9ecc964788)
- [BLADE-545] disable formField for CI (5962aaf334)
- [BLADE-545] upload regardless (02c5d2c0df)
- [BLADE-545] always upload tests (75e8fed482)
- [BLADE-545] remove incorrect .blade settings dir (11c9b721a8)
- [BLADE-544] Need version for form field smoke test (35affcbe83)
- [BLADE-544] Separate recursive option (a6915cf571)
- [BLADE-544] Update existing tests.zip (375d80c1fe)
- [BLADE-544] buildDir does not include libs (32ff6fc9fd)
- [BLADE-544] Missing task declaration (1cca187a9b)
- [BLADE-544] Archive failing test projects (b4592fda74)
- [BLADE-544] Add form field smoke test (6694bcf0ac)
- [BLADE-544] Add form field test (ad3b2ad240)
- [BLADE-544] Simplify tests in project (c7b2733ad0)
- [BLADE-544] No longer needed (1a0f2bba0c)
- [BLADE-544] 7.3 is now default (c792f4c157)
- [BLADE-544] Wordsmith (f44a42e90c)
- [BLADE-544] TP is now a default setting for Workspace (b4d1380931)
- [BLADE-544] Updated workspace version (103ce40e27)
- [BLADE-544] Integrate latest project templates (755b9ef45e)
- [BLADE-533] try to fix source formatting (3d3c667771)
- [BLADE-533] quote args (a15597cc70)
- [BLADE-533] add smoke tests (18b4aab3a6)
- [BLADE-533] grab embedded extensions during template execution (cceaf944d0)
- [BLADE-542] hide outputs command (2ab8cf679a)
- [BLADE-542] just show the gradle output because it is more useful (bedc1b5ed4)
- [BLADE-542] STOPPED logs were printed twice (0f76d6eb60)
- [BLADE-533] fix tests (81c8fe4d34)
- [BLADE-533] fix logic (5ddb199b25)
- [BLADE-533] embed js-* project templates into blade.jar (e2d4481316)
- [BLADE-543] fix publish args checking (ad62ec2ec5)
- [BLADE-543] update to latest jpm snapshot and latest blade release
(671b112281)
- [BLADE-543] remove global installers we should only support local (378b158ed1)
- [BLADE-533] fix resource path (8db39f1ead)
- [BLADE-533] fix js-theme project generation and test (b89873668d)
- [BLADE-533] use dir (884bcf8e9f)
- [BLADE-533] assert build task (cf31c77a2c)
- [BLADE-533] switched to Liferay version instead of generator version
(e0f3adc8dd)
- [BLADE-533] fix tests (54268d8536)
- [BLADE-533] small fix for case where .blade/cache/node exists but has no files
(63ece8b5ee)
- [BLADE-533] js-theme/js-widget test scaffolding (9df0e9f40c)
- [BLADE-533] we are adding new functionality to lets do minor bump (e2f1dfa07f)
- [BLADE-533] temporarily remove until first snapshots are available
(19b8836648)
- [BLADE-533] add to blade extensions (a6bbeaa274)
- [BLADE-533] inline methods (274fdea269)
- [BLADE-533] consolidate repository config (ece087c6b8)
- [BLADE-533] use pluginManager (cf1b708036)
- [BLADE-533] remove ignore (90cc1da6fb)
- [BLADE-533] should automatically reinstall yo generators if package.json
changes (342fe99df8)
- [BLADE-533] wrong base dir (2eced4a007)
- [BLADE-542] fix typo (98ce797c95)
- [BLADE-533] no longer need projectType (d4da4da1f6)
- [BLADE-542] fix gradle script (fb0ca91992)
- [BLADE-533] use liferayVersion instead of generator version (01b4685c06)
- [BLADE-542] ignore broken test (38e19679bd)
- [BLADE-533] update to generator-liferay-theme 8.2.0 (79a1ae1b25)
- [BLADE-542] Ignore broken test (4a4b45b9b9)
- [BLADE-533] use the right generator based on liferayVersion (97b69c6536)
- [BLADE-533] skip install (0b09dcd261)
- [BLADE-533] use yo generator-liferay-theme 10.0.1 (3ca5fdc66c)
- [BLADE-533] deploy task for extensions (e082621751)
- [BLADE-533] add project-template-js-theme (bddf84dcdf)
- [BLADE-533] fix runYo in unix (643f3afd06)
- [BLADE-533] add generator-liferay-theme 10.x support (1a8bc43f8c)

### Dependencies
- [BLADE-573] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.17.
- [BLADE-566] Update the com.liferay.project.templates dependency to version
5.0.65.
- [LPS-114909] Update the com.liferay.project.templates dependency to version
5.0.60.
- [BLADE-538] Update the com.liferay.project.templates dependency to version
5.0.56.
- [LPS-111461] Update the com.liferay.project.templates dependency to version
5.0.51.
- [LPS-111461] Update the com.liferay.project.templates dependency to version
5.0.49.
- [LPS-114169] Update the com.liferay.project.templates dependency to version
5.0.47.
- [BLADE-554] Update the com.liferay.project.templates dependency to version
5.0.45.
- [LPS-114088] Update the com.liferay.project.templates dependency to version
5.0.43.
- [LPS-114088] Update the com.liferay.project.templates dependency to version
5.0.42.
- [BLADE-550] Update the com.liferay.project.templates dependency to version
5.0.41.
- [BLADE-549] Update the com.liferay.project.templates dependency to version
5.0.39.
- [BLADE-544] Update the com.liferay.project.templates dependency to version
5.0.37.
- [BLADE-533] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.0-SNAPSHOT.
- [BLADE-533] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.0-SNAPSHOT.
- [BLADE-533] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.0-SNAPSHOT.
- [BLADE-533] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.0-SNAPSHOT.
- [BLADE-533] Update the com.liferay.project.templates.js.theme dependency to
version 1.0.0-SNAPSHOT.
- [BLADE-533] Update the com.liferay.project.templates.js.widget dependency to
version 1.0.0-SNAPSHOT.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.17-SNAPSHOT.

## 3.9.2 - 2020-04-10

### Commits
- [BLADE-539] Blade should use same way to handle missed .blade.properties
(6025f503f7)
- [BLADE-539] automatically migrate maven profile settings if quiet arg is set
(d595ea8548)
- [BLADE-537] fix test (6e95f7f7c7)
- [BLADE-537] show description for commands with missing required parameters
(d236c738b4)
- [BLADE-533] remove packages no longer needed (181bd39c6a)
- [BLADE-533] switch back to binary wrapper (2599d05b7e)
- [BLADE-533] add check for js-theme (f4f416f62a)
- [BLADE-533] use runYo (c9087026d1)
- [BLADE-533] add runYo (f9dfdcb0db)
- [BLADE-533] reduce duplicate code (40e57380f8)
- [BLADE-533] support yo generators 8.x and 9.x (d3e27544b2)
- [BLADE-533] add getBladeCacheDir method (85c52e3b6b)
- [BLADE-533] move util classes into cli (2e03c9c20c)
- [BLADE-535] put conditional on job (4c15b0478d)
- [BLADE-535] only run publish on gamerson/liferay-blade-cli fork (ae9475b6fd)
- [BLADE-535] debug publish output (ffaf63d1c5)
- [BLADE-535] check isQuiet for all optional output in commands (fd48b38cd3)
- [BLADE-535] add a --quiet option (a4ecd6fb9c)
- [BLADE-533] use global instance for template name validator (a35649ffeb)
- [BLADE-533] more asserts in test (12f4e39198)
- [BLADE-533] add blade extensions to project template args earlier (6b668c2966)
- [BLADE-533] failing test case (05e41910b2)
- [BLADE-488] rename (11945ee7c4)
- [BLADE-488] add lines() to allowed chains (fb29d2d72f)
- [BLADE-488] use try-with-resources (835699b7cd)
- [BLADE-488] Update java 11 test case (ad1daea23b)
- [BLADE-488] More problems introduced in upgrading to 7.3 GA1 (6a8a02983d)
- [BLADE-488] Fix test for removed scopes (300c23d78f)
- [BLADE-488] Remove scopes from properties (f325810e25)
- [BLADE-488] No longer needed (fb8945c3c1)
- [BLADE-488] Update test (385837212a)
- [BLADE-488] Add more legacy properties in test (5f0b04c91f)
- [BLADE-488] Match properties with scopes (8b202cc7e0)
- [BLADE-488] Improve logic (b52f216b3f)
- [BLADE-534] fix repoUrl (b994b99f8e)
- [BLADE-534] fix args (def3d59bb6)
- [BLADE-534] use remote (2e1d0efa74)
- [BLADE-534] add secrets into ENV (097a8f6e39)
- [BLADE-534] add publish workflow action on push to master (048e08a0e2)
- [LPS-105747] If there are more tickets, we should generate the link for each
of them (970d976cdc)
- [LPS-105747] Remove colon, since we've already finished referring the link
(c81368426c)
- [LPS-105747] Fix Markdown syntax (50b45a0991)
- [LPS-110131] fix tests (d6528f3eac)
- [BLADE-503] reenable test (ec1bf9e98f)
- [BLADE-503] remove catch (cc44bdfaa8)
- [BLADE-503] remove test (10875bae67)
- [BLADE-503] Remove unused code (aff13002cf)
- [BLADE-503] refactor Prompter to not assume System.in and add test for
interactive mode (086ebd5e18)
- [BLADE-503] Use reflection to analyze Project Templates Ext for Interactivity
(b7d816fa98)
- [LPS-105747] Comment out Markdown Links (d8fef71ac6)
- [LPS-105747] Use github markdown syntax (c06eee3618)
- [LPS-105747] Remove unnessesary code (e3d8aa4c01)
- [LPS-105747] Source formatting (1bc3758ddb)
- [LPS-105747] Make some changes for the current project (4b6c2431b5)
- [LPS-105747] Add build-buildscript.gradle (a37257cfc9)
- [LPS-105747] Copy from liferay-portal (35c1c31b09)
- [BLADE-532] sort commands by name (d605b88d81)
- [BLADE-522] Look for both Location and location (d8fdba3cba)
- [BLADE-522] Fix LinkDownloader for updated Github header (caa014f775)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.16.
- [LPS-110131] Update the com.liferay.project.templates dependency to version
5.0.31.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.16-SNAPSHOT.

## 3.9.1 - 2020-02-25

### Commits
- [BLADE-520] fix SF (fa386a3770)
- [BLADE-520] Update test (07ee304d28)
- [BLADE-520] Use com.liferay.gradle.plugins.workspace 2.2.6 (bd9dcedcc3)
- [BLADE-510] use double quotes instead of single (f383241814)
- [BLADE-510] Better dependency resolution process (9dca9979c0)
- [BLADE-513] fix tests (5c09faa51f)
- [BLADE-513] update tests to use 7.3 (1be4692714)
- [BLADE-513] add support for 7.3 options (4e67f8bdaa)
- [BLADE-509] fix tests (8849b673b6)
- [BLADE-509] just always throw exception so it can be printed (37ab2e3521)
- [BLADE-509] fix pluginsSdkDir checking logic (5eebe83726)
- [BLADE-509] failing test case for convert command (d9462b3ee6)
- [BLADE-509] convert command should be more careful when searching for plugin
source (37eefdc550)
- [BLADE-501] [BLADE-504] redundant (11911d0fde)
- [BLADE-501] [BLADE-504] Don't print error stacktrace for gradle executions we
just let original error be seen (cbe46d1073)
- [BLADE-501] [BLADE-504] Fix gradle test (3025ce27bc)
- [BLADE-504] [BLADE-501] Verify error and return code (f20fd778e6)
- [BLADE-501] [BLADE-504] Don't swallow gradle errors and return codes
(f70b6f0b92)
- [BLADE-505] correctly exit script if tests fail (e4bc91df37)
- [BLADE-505] Update to Project Templates 5.0.19 (16e16991d7)
- [BLADE-502] Revert "BLADE-502 failing test cases" (92c464e071)
- [BLADE-502] fix bash script (c624f08f61)
- [BLADE-502] failing test cases (8d85b39126)
- [BLADE-502] test scripts update (b75dc4970e)
- [BLADE-502] archive tests if we have failures (5849a7545d)
- [BLADE-502] ignore failing test on windows (5cfa62c3c1)
- [BLADE-502] archive tests zip file if tests fail (eb46ca5417)
- [BLADE-418] use ${project.version} (d50012c5a2)
- [BLADE-418] normalize API (643fb00c33)
- [BLADE-418] sort (28f0b9f504)
- [BLADE-418] update template to gradle5 build (a4e791ca3e)
- [BLADE-502] normalize var names (0d2d497ca3)
- [BLADE-502] remove newline (7cfde50ffc)
- [BLADE-502] Make Linux versions the same (238797209d)
- [BLADE-496] Print error output (a02964a065)
- [BLADE-496] ignore warnings (1efd53ce97)
- [BLADE-496] Use built-in java for md5 (41a66690f6)
- [BLADE-502] Add JDK 11 support to Azure Pipeline tests (7485b993aa)
- [BLADE-496] Update dependencies for JDK11 (7a74f59534)
- [BLADE-496] Update code to support JDK11 (18f7e3488f)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.15.
- [BLADE-520] Update the com.liferay.project.templates dependency to version
5.0.25.
- [LPS-108630] Update the com.liferay.project.templates dependency to version
5.0.23.
- [BLADE-510] Update the ant dependency to version 1.10.7.
- [BLADE-513] Update the com.liferay.project.templates dependency to version
5.0.21.
- [BLADE-507] Update the com.liferay.project.templates dependency to version
5.0.20.
- [BLADE-505] Update the com.liferay.project.templates dependency to version
5.0.19.
- [] Update the commons-compress dependency to version 1.18.
- [BLADE-496] Update the powermock-api-easymock dependency to version 2.0.4.
- [BLADE-496] Update the powermock-classloading-xstream dependency to version
2.0.4.
- [BLADE-496] Update the powermock-module-junit4 dependency to version 2.0.4.
- [BLADE-496] Update the powermock-module-junit4-rule dependency to version
2.0.4.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.15-SNAPSHOT.

## 3.9.0 - 2020-01-23

### Commits
- [BLADE-500] Update to latest gradle-credentials-plugin (ee2ef0134e)
- [BLADE-499] FS (df8f1e519c)
- [BLADE-499] Fix failing test with updated workspace dependency (8838cbe91c)
- [BLADE-499] Update to Project Templates 5.0.18 (0287e8d28f)
- [BLADE-493] fix publish testing script (f43be7b73a)
- [BLADE-493] fix jar basename (71e82140cb)
- [BLADE-493] update to version 3.9.0-SNAPSHOT (3d54402e63)
- [BLADE-493] fix sample test (9f49c33503)
- [BLADE-493] remove 4.10.2 gradle wrapper (792e8980e1)
- [BLADE-493] upate to gradle 5 API (39e33c13b1)
- [BLADE-493] fix jar version (c67cd57df6)
- [BLADE-490] update to gradle5 blade build (2d69c86d67)
- [LPS-105502] correct tests (113f8c28c9)
- [LPS-105502] no longer needed (32825d8f6a)
- [LPS-105502] Use gradle 5 in project templates (2a58913f13)
- [BLADE-489] Update Gradle Wrapper Version (bc84bbe6b3)
- [LRDOCS-7448] Update description (32ff7c903d)
- [BLADE-487] fix workspace plugin version in test to avoid collision
(fb1af68ae3)
- [BLADE-486] don't autoclose FileSystem (b3092d41c8)
- [BLADE-486] ignore eclipse bin folder (2defde4b3d)
- [BLADE-486] logic improvements (f7e03f1921)
- [BLADE-486] Use try-with-resources (5a7b7f1954)
- [BLADE-486] anyMatch is more concise (6354fd2965)
- [BLADE-486] emptyList is immutable (9669a2953d)
- [BLADE-486] switch system.out/err to use blade streams (822b4a5193)
- [BLADE-486] improve project searching capablities (0978956b4e)
- [BLADE-486] make all arguments additive instead of overwriting (eeaf409fc7)
- [BLADE-486] rename to --skip-init and default to false (6879c0136e)
- [BLADE-486] add -f and -i shortnames (d71e4d42a6)
- [BLADE-486] rename to use --fast-paths and --ignore-paths (6987982287)
- [BLADE-486] ignore restriction (904af1a7b4)
- [BLADE-486] gw formatSource (82fbaf8407)
- [BLADE-486] optimize watch command for large gradle projects (36b22d2a97)
- [BLADE-486] add classes to ignore (0f2be7b308)
- [BLADE-486] provide option to skip initial deploy (75318a0cb2)
- [BLADE-486] change watch command to use deploy and deployFast instead of watch
(6d0d3ba8cd)
- [BLADE-487] semver (01320997b0)
- [BLADE-487] load extensions from gradle or project objects (912a680eeb)
- [BLADE-487] update tests with local workspace plugin (91a1021b06)
- [BLADE-487] get dockerImageId and dockerContainerId from ProjectInfo model
(a2ea83322c)
- [BLADE-483] simplfy and add API for getting snapshot/release version from
executed UpdateCommand (a4158e74f6)
- [BLADE-483] Fix tests and display new releases for snapshots (c9be2fc8a4)
- [BLADE-483] Fix unquoted string in publish.sh (f9589b82bd)
- [BLADE-483] Change up2date checker (2eda4f4077)
- [BLADE-483] UpdateCheck message changes (02891f3e0e)
- [BLADE-483] Message fixes (0918e72d89)
- [BLADE-483] Fix remaining two issues, snapshot to release and same version to
same version (e31ee23d87)
- [BLADE-483] Refactor '_shouldUpdate' logic slightly (c12c38a385)
- [BLADE-483] Fix minor typo, only check MD5 if major versions match
(8d10669129)
- [BLADE-483] Clarify messages and fix snapshot comparison (061633a402)
- [BLADE-483] Fix MD5 matching when using snapshots (ee1911b313)
- [BLADE-483] Fix remaining issues and confusing errors (9fb48a2d7d)
- [BLADE-483] Fix issue with snapshot updates (12940b9464)
- [BLADE-483] rename class (dbc3b057dd)
- [BLADE-483] remove eclipse warnings (198fa9e3e1)
- [BLADE-483] add exit code back to correctly signal to calling process
(111b294308)
- [BLADE-483] remove ignore (32e5fadc33)
- [BLADE-483] rename (2febd5c8b3)
- [BLADE-483] declare this variable closer to where it is used (6f16c8de7b)
- [BLADE-483] rename (a28187e254)
- [BLADE-483] make private constants (d49fe0bca3)
- [BLADE-483] Refactor and update tests (01818d402b)
- [BLADE-483] simpify and just use a predicate class (0b5267352e)
- [BLADE-483] sort (f0d3b325c7)
- [BLADE-483] Allow ability to skip uploading build scans (e4c2e36a15)
- [BLADE-483] Fix tests (4ebd18594d)
- [BLADE-483] update should stay on same branch (snapshot, release) unless
specified explicitly (1268818d27)
- [BLADE-483] Implement new validation framework checking (617c2f6ea5)
- [BLADE-483] Add validation to UpdateArgs and add new parameter (858ae20e11)
- [BLADE-483] Add full argument validation framework (cda046eee1)
- [BLADE-483] Assert BUILD SUCCESS only (aaba05c0ae)
- [BLADE-483] Remove unnecessary test (895ac782b6)
- [BLADE-483] Refactor unzipManifest (abc39235ab)
- [BLADE-483] Fix Naming (28e801840c)
- [BLADE-483] Remove unnecessary file (6e20c3d4d6)
- [BLADE-483] Copy manifest in tests and use it to read version (7cd40959c3)
- [BLADE-483] Make --check hidden (6f39d00ca6)
- [BLADE-483] Switch if check in publish script (89b51c73bb)
- [BLADE-483] Properly detect custom URL (f72a835bdb)
- [BLADE-483] Add support for skipping tests (8f6e610a14)
- [BLADE-483] Additional MD5 support. (72b92c7676)
- [BLADE-483] Add MD5 Verification (c5265371df)
- [BLADE-475] Fix gradle workspace version test (fb959c2927)
- [BLADE-475] Use project templates 5.0.1 (a4c9bd668f)
- [BLADE-475] Add Project Template 5.0 support (4da66ebfde)
- [BLADE-475] initial work to adopt project templates 5.0.0 api (9bd91ea545)
- [BLADE-482] Update to bnd 4.3.0 (fd824d780e)
- [BLADE-474] simplify (7bae636837)
- [BLADE-474] Support latest.integration and add a test (e92d01585a)
- [BLADE-474] Simplify (c196815f64)
- [BLADE-474] Properly detect workspace version as latest.release (abebcf4ce2)
- [BLADE-481] Test (dd258e3e83)
- [BLADE-481] Remove logic from TestUtil (1213ccb43a)
- [BLADE-481] Add exception handling logic to TestUtil as well (3757349e00)
- [BLADE-481] Move BridJ exception handling to BladeTest (be6a791f90)
- [BLADE-481] Swap if statement (0361d6c8f9)
- [BLADE-481] Ignore BridJ intermittent erroneous failure (12360baff4)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.14.
- [BLADE-499] Update the com.liferay.project.templates dependency to version
5.0.18.
- [BLADE-493] Update the gradle-base-services-groovy dependency to version
5.6.4.
- [BLADE-493] Update the gradle-core dependency to version 5.6.4.
- [BLADE-493] Update the gradle-tooling-api dependency to version 5.6.4.
- [BLADE-493] Update the com.liferay.project.templates dependency to version
5.0.11.
- [LPS-105502] Update the com.liferay.project.templates dependency to version
5.0.8.
- [BLADE-475] Update the com.liferay.project.templates dependency to version
5.0.1.
- [BLADE-475] Update the com.liferay.project.templates dependency to version
5.0.0.
- [BLADE-482] Update the biz.aQute.bnd.gradle dependency to version 4.3.0.
- [BLADE-482] Update the biz.aQute.bndlib dependency to version 4.3.0.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.14-SNAPSHOT.

## 3.8.0 - 2019-09-19

### Commits
- [BLADE-221] Specifying invalid options should display an error (209be8edf6)
- [BLADE-221] normalize (5fe764d620)
- [BLADE-221] readd test (1d7b67c788)
- [BLADE-221] rename to ParameterPossibleValues also change annotation to only
require a supplier (5a11159915)
- [BLADE-221] rename to ParameterPossibleValues (034a2b730f)
- [BLADE-221] use constants (713ae7c5d3)
- [BLADE-221] Handle multiple main parameters by throwing ParameterException
(2fe92f82fa)
- [BLADE-221] Fix Tests (13b126c316)
- [BLADE-221] Refactor code (fe00fffbfe)
- [BLADE-221] Fix smoke tests (339bb1d7d0)
- [BLADE-221] Re-arrange arguments and re-word messages (9936df055d)
- [BLADE-221] Add InputOptions annotation and validators (550b4967f2)
- [BLADE-221] Fix tests, throw exception without interactive console
(1e5caec697)
- [BLADE-221] Prompt for missing required arguments (924e053b1b)
- [BLADE-468] Fix gradle test (00efddb3ae)
- [BLADE-468] Fix extension sample tests (40c4b1e2a7)
- [BLADE-468] Add -v option in maven tests (f0218f4cb4)
- [BLADE-468] Add -v option in cli tests (86a35bcacc)
- [BLADE-468] Always prompt user for Liferay Version when initializing a new
workspace (db19d04614)
- [BLADE-467] Add test case to test targetplatform projects (d8fc4515e8)
- [BLADE-467] Revert ffc6a0820c6977ac291cd6bd530a8d8e386f4261 (fa80ebeb0f)
- [LPS-98820] Specify bnd.annotation in service builder templates (f1167725a5)
- [BLADE-464] Bump minor version (a61951da6f)
- [BLADE-464] Set project template args (c56f14c201)
- [BLADE-464] Set default version to 7.2 (ffc6a0820c)
- [BLADE-464] Typo in test (d21015f831)
- [BLADE-464] Update test (114a785b76)
- [BLADE-464] New parameters for Service builder template (5ec9cf6e07)
- [BLADE-464] Add parameters for spring mvc portlet (2119eea2ec)
- [BLADE-464] Moved FileUtil (3f846fe26b)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.13.
- [] Update the com.liferay.project.templates dependency to version 4.5.3.
- [LPS-98820] Update the com.liferay.project.templates dependency to version
4.3.3.
- [BLADE-464] Update the com.liferay.project.templates dependency to version
4.3.1.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.13-SNAPSHOT.

## 3.7.4 - 2019-07-23

### Commits
- [BLADE-461] Use project templates 4.2.27 (591d642954)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.12.
- [BLADE-461] Update the com.liferay.project.templates dependency to version
4.2.27.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.12-SNAPSHOT.

## 3.7.3 - 2019-06-24

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.11.
- [] Update the com.liferay.project.templates dependency to version 4.2.26.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.11-SNAPSHOT.

## 3.7.2 - 2019-06-19

### Commits
- [BLADE-441] Use project-templates 4.2.25 to include naming standards fix
(036a43bc5b)
- [BLADE-452] use canonical path (d8c7038513)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.10.
- [BLADE-441] Update the com.liferay.project.templates dependency to version
4.2.25.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.10-SNAPSHOT.

## 3.7.1 - 2019-06-17

### Commits
- [BLADE-441] Use project-templates 4.2.25 for naming standards fix (5734917a79)
- [BLADE-400] install blade into docker container and then test update to just
built version (5d68345667)
- [BLADE-400] update url (772262ce7e)
- [BLADE-441] Update InitCommandTest with updated links (9f3db4143e)
- [BLADE-441] Upgraded to workspace 2.0.4 (d182be2aa8)
- [BLADE-441] Use project-templates 4.2.24 for service builder TP fix
(40b70fa6a6)

### Dependencies
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.9.
- [BLADE-441] Update the com.liferay.project.templates dependency to version
4.2.24.
- [BLADE-441] Update the com.liferay.blade.extensions.maven.profile dependency
to version 1.0.9-SNAPSHOT.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
latest.release.

## 3.7.0 - 2019-06-03

### Commits
- [BLADE-442] fix the conflict between argument remove and custom name
(f69e58cfdb)
- [BLADE-436] Add 7.2 back (cd68f33019)
- [BLADE-440] Print maven output and execute all tasks at once (2bf8bce7c0)
- [BLADE-440] Add @BladeProfile annotation to new command (fb5cd45705)
- [BLADE-440] Add maven deploy support (e294148732)
- [BLADE-423] fix issue (ce2d79ca15)
- [BLADE-423] add previous constructor (658c858c61)
- [BLADE-423] add parameter to enable not remove source project (f0426776cb)
- [BLADE-431] Fix new test on Windows (f4361ebc9b)
- [BLADE-431] Update project templates to 4.2.14 (4e898fc904)
- [BLADE-425] Print warning if can't locate extension (b2f985b3f7)
- [BLADE-425] string builder var names can be shortened to just sb (c3e17fce6b)
- [BLADE-425] Add nested try-catch (911d547d9a)
- [BLADE-425] Don't fail to load if an embedded extension fails (776942481e)
- [BLADE-408] give a message if Node.JS is not installed (6f8d6e3b35)

### Dependencies
- [] Update the com.liferay.project.templates dependency to version 4.2.19.
- [] Update the com.liferay.project.templates dependency to version 4.2.17.
- [] Update the com.liferay.project.templates dependency to version 4.2.15.
- [BLADE-431] Update the com.liferay.project.templates dependency to version
4.2.14.

## 3.6.0 - 2019-04-12

### Commits
- [BLADE-412] Refactor and rename some packages (175d55882e)
- [BLADE-412] publish changes for remote deploy (4c659dbfcc)
- [BLADE-412] remove new lines (0f72dcab47)
- [BLADE-412] rename var (05e5513bf2)
- [BLADE-412] use as a list object since it matches underlying instance
(6db3e7ad65)
- [BLADE-412] inline vars (c544cc7f66)
- [BLADE-412] remove newlines (b16e5a4bec)
- [BLADE-412] Refactor handling of git subdirectories (215f28bce5)
- [BLADE-412] Deploy Remote Extension (48dea0e3ee)
- [BLADE-412] Add ability to install extensions in subdirectories (3d5c657b47)
- [BLADE-411] Remove newlines, add new filtering and refine buildService test
(a143f3f03f)
- [BLADE-411] Ignore warnings (894f48f6bd)
- [BLADE-411] Print Error (9744395241)
- [BLADE-411] Remove new lines (0a569650ac)
- [BLADE-411] Add buildService test (549a1568e5)
- [BLADE-411] print exec args (1d9f422759)
- [BLADE-411] rename method (9bffaacb8a)
- [BLADE-411] extract to interface (90ae7eea89)
- [BLADE-411] buildService maven command (b786922d69)
- [BLADE-409] make sure that the length equals to 3 (0ebafe2287)
- [BLADE-409] rename (d376ceb424)
- [BLADE-409] support migration of portal jars in
liferay-plugin-package.properties (ead3a15fbf)
- [BLADE-407] bump to 3.6.0-SNAPSHOT (eb21f04181)
- [BLADE-407] add test (3c9223d937)
- [BLADE-407] re-word (f1c99a6bf4)
- [BLADE-407] add "source" parameter to convert command (e59c8df204)
- [BLADE-373] remove from API (7adcce139d)
- [BLADE-373] Rebase on master (84b0257083)
- [BLADE-373] Change .blade/settings.properties to .blade.properties
(eb42f986ba)
- [BLADE-392] suppress CDNCheck (013ad9e7a4)
- [BLADE-392] inline (08d4b56aa2)
- [BLADE-398] Blade Server Start / Run Custom Port Tests (7f3ae6cd89)
- [BLADE-392] Fix Server Start / Run for Windows (8b4839323f)
- [BLADE-392] Fix maven server run, refactor and improve tests, test server run
(d5442c070c)

## 3.5.0 - 2019-02-25

### Commits
- [BLADE-397] Fix blade server start -t for wildfly (874081a134)
- [BLADE-389] Improve Prompter and fix hang (52131fb22f)
- [BLADE-395] -p, --port, -s, --suspend features added for blade server start
and run (633dc87461)
- [BLADE-394] Use NIO, fix naming, final touches (4ac19ba2b6)
- [BLADE-394] Refactor Server Init Test and naming conventions (d827882703)
- [BLADE-394] Add Wildfly Debug Test (8226e2d0f9)
- [BLADE-394] Add Wildfly Test Refactor (36f1e3b1f7)
- [BLADE-394] Add Tomcat Debug Test (0adcedf1b8)
- [BLADE-394] Refactor to support debug tests (f6a749973c)
- [BLADE-394] Refactor Server Tests (799e09273d)
- [BLADE-390] Parallel tests now require a -Pparallel flag (16d77e59fb)
- [BLADE-343] bump to 3.5.0 (d9e4d322e3)
- [BLADE-343] Improve naming (bd4dba3986)
- [BLADE-343] Add Test (8c0fcf345f)
- [BLADE-343] Pass in environment param when running blade server init
(6870cda3e2)
- [BLADE-387] Add missing newline (8b24cf5716)
- [BLADE-387] bump maven profile (bc73441294)
- [BLADE-387] enable testing into local snapshot docker test (da41a4876b)
- [BLADE-387] Fix up package naming (e86720fb66)
- [BLADE-387] Bad Extension Test (1b4da19238)
- [BLADE-387] Allow blade to run even if an extension doesn't load properly.
(45d5da54cc)
- [BLADE-388] make snapshots testable (75d5f921f1)
- [BLADE-388] only use mavenLocal for testing everything else should use normal
publishing to non-cdn repos (76e3374352)
- [BLADE-385] rename (6c35bf7ff5)
- [BLADE-385] simplify (ba6c606c84)
- [BLADE-385] normalize (d1ebf9c7d7)
- [BLADE-385] Fix CreateCommandMaven issue and test (71f04f976a)
- [BLADE-385] Re-enable GadlePrintErrorTest (a730f31ebd)
- [BLADE-385] Fix remaining tests (d5685ccdce)
- [BLADE-385] re-enable test (ed87081e88)
- [BLADE-385] ignore test (079a5ad7b8)
- [BLADE-385] test no longer valid (e6fbd04c1e)
- [BLADE-385] refactor classloader methods (3044fc9bff)
- [BLADE-385] refactor WorkspaceProvider API (67946a0ffa)
- [BLADE-385] ignore serial warning (56d8b58d7f)
- [BLADE-385] remove cycle (1c7b6e46c4)
- [BLADE-385] call run-tests.bat from appveyor (e48f832a1f)
- [BLADE-385] fix windows bat (e2a2160bda)
- [BLADE-385] enable scan (a2b67195d6)
- [BLADE-385] fix SF (8835824c39)
- [BLADE-385] script to run tests (1b2489a086)
- [BLADE-385] apply SF to all projects (99197733eb)
- [BLADE-385] normalize repos per project (69dc545b0b)
- [BLADE-385] collect all smoke tests into task "smokeTests" (47fc60b431)
- [BLADE-385] remove uneeded tests (45924edc63)
- [BLADE-385] this is covered in smoke tests (2bc5246865)
- [BLADE-385] remove mavenLocal (be8842ada3)
- [BLADE-385] Better Profile Workspace handling / detection (ad808bb3a1)
- [BLADE-386] use mvc-portlet and rename scripts (c9966fd3d3)
- [BLADE-386] improve snapshots (785aa32491)
- [BLADE-386] remove mavenRepo (be3ed2b6dd)
- [BLADE-386] remove duplicate build tests (a5e70827fd)
- [BLADE-386] these verification builds will be covered in project-templates
(3bbf4add5e)
- [BLADE-383] read annotation instead of duplicating (9bc52e2492)
- [BLADE-383] swith to private instead of protected (ce9f005448)
- [BLADE-383] Fix unwanted stack traces (915760edcb)

### Dependencies
- [] Update the com.liferay.project.templates dependency to version 4.2.9.
- [BLADE-389] Update the commons-io dependency to version 2.6.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
latest.integration.
- [] Update the com.liferay.blade.extensions.maven.profile dependency to version
1.0.6+.

## 3.4.3 - 2019-01-22

### Commits
- [BLADE-377] use two command build to ensure maven profile is included in
blade.jar (0fce29278f)
- [BLADE-377] Add support for -b (9f8aa7e39e)
- [BLADE-381] : 'blade update' should not run the updateCheck (1e7de5b825)
- [BLADE-380] logic fix (055162245a)
- [BLADE-380] allow maven init command to locate a workspace (4a3823c6e9)
- [BLADE-380] switch to snapshots (cb5de58a13)
- [BLADE-380] rename (ba67567805)
- [BLADE-380] normalize variable names (bb96b8e733)
- [BLADE-380] remove chaining (8979556456)
- [BLADE-380] Refactor tests to use TestUtil (6c3fb96f5a)
- [BLADE-380] Separate Settings and Extensions parent dirs (ca2a501d02)

## 3.4.2 - 2019-01-18

### Commits
- [BLADE-346] Update project template version (95d4d123bc)
- [BLADE-377] normalize variable names (93450b1453)
- [BLADE-377] refactor command methods to BladeCLI (e8ca57cb59)
- [BLADE-377] ignore /bin/ error (34d836319e)
- [BLADE-377] Fix tests (b32c490347)
- [BLADE-377] Fix Maven Profile and Tests (25d6061c2f)
- [BLADE-377] Refactor to use -P, BladeCLI not depend on InitCommand
(9d4f0dd38f)
- [BLADE-377] init command should honor the -p, --profile-name flag over any
defaults (276cd50b43)
- [BLADE-361] unused (e26811c911)
- [BLADE-361] Remove unused parameter (4828295c82)
- [BLADE-378] rename (ef9f99e1a0)
- [BLADE-378] : update gives confusing message when switching to released
version (06614a901e)
- [BLADE-361] Fix Windows Test (0b81eecc29)
- [BLADE-361] Refactor to always use deploy task (614ec60c7a)
- [BLADE-361] Additional requested changes (00ca02d147)
- [BLADE-361] Additional API change (b1084a9d07)
- [BLADE-361] necessary API change (2e77dce549)
- [BLADE-361] Additional requested changes (c9d529cf5b)
- [BLADE-361] Blade Deploy API changes (5b18295bed)

### Dependencies
- [BLADE-346] Update the com.liferay.project.templates dependency to version
4.2.6.

## 3.4.1 - 2018-12-18

### Commits
- [BLADE-319] update sf dependency (9b671de13a)
- [BLADE-319] simplify test (e8b94e1c1a)
- [BLADE-319] reorder (288f81ab84)
- [BLADE-319] : blade create does not handle modules.dir property with multiple
values (a090b10629)
- [BLADE-371] rename file (a6f285b8b2)
- [BLADE-371] configure smoke tests tasks by name (e66a4eb9da)
- [BLADE-371] Change error parsing (fcb77fa489)
- [BLADE-371] Fix task ordering (02f4665d23)
- [BLADE-371] Simplify (bedb970df7)
- [BLADE-371] Move version test (f5fa34bac3)
- [BLADE-371] refactor tasks (5c2a352af3)
- [BLADE-371] Add smoke tests (ac2e524743)
- [BLADE-375] update gradle tooling api (0e40629668)
- [BLADE-375] use specific version to speed up build (3cf16dfcd7)
- [BLADE-370] Fix Maven Server Tests on Windows (3375cf46e7)
- [BLADE-370] just mvnw should be enough (e83318ab8a)
- [BLADE-370] add maven server start test (4ecb0665ee)
- [BLADE-370] rename package (636caa3d9d)
- [BLADE-370] add server run command maven (ea5b213fde)
- [BLADE-370] Fix maven server commands (973796fa78)
- [BLADE-370] Fix Maven Profile copying to folder (d61c327197)
- [BLADE-370] encode blade extensions into properties file (1b3143a098)
- [BLADE-372] wordsmith (cd25370249)
- [BLADE-372] : daily update check gives wrong message when using snapshots
(762c98e576)
- [BLADE-354] - blade init will create new workspaces even if it is inside of
another workspace without throwing an error (ad383e4f2a)
- [BLADE-370] include maven-profile-1.0.0-SNAPSHOT.jar (f66a6f4356)
- [BLADE-369] Use project templates 4.2.4 (da1c1bcfca)
- [BLADE-245] rename (ad4fbf7ec6)
- [BLADE-245] Refactor LocalServer (539d19f0d5)
- [BLADE-245] igore spurious errors (668c46cd8e)
- [BLADE-245] kill tomcat/wildfly first (304cc53192)
- [BLADE-245] add bin (3093327337)
- [BLADE-245] show process list (ba755b85a4)
- [BLADE-245] add server run command and wildfly test (3a87d2c8cb)
- [BLADE-245] create a specific StartServerCommandTest (5722cc69db)
- [BLADE-245] use our own class to list current JVMs intead of 3rd party lib
(eb101b61e5)
- [BLADE-245] refactor server run/stop commands (a208a15bd2)
- [BLADE-245] extract new LocalServer command to encapsulate server behavior
(70089b48b9)
- [BLADE-245] refactor into two commands blade server start, blade server run
(3c8540c517)
- [BLADE-245] reduce uncessary API surface (780a13fa86)
- [BLADE-245] refactor get args (95557696a6)
- [BLADE-367] simplify (df473a3e8b)
- [BLADE-367] rename task (e2e102ae2f)
- [BLADE-367] maven-profile.jar fix recursive inclusion (7b43f67819)
- [BLADE-366] Reduced number of templates (d6a45411c3)
- [BLADE-366] Missing slash (4ddfa6d76d)
- [BLADE-366] Format Source (b114dd0ad9)
- [BLADE-366] Fix tests (ce9be9683c)
- [BLADE-366] Update project templates to 4.2.3 (c47982e7f4)
- [BLADE-358] : if I'm on a snapshot version, the automatic 'checkForUpdate'
doesn't prompt if a new snapshot is available. fix for shouldUpdate.
(2709e54a96)
- [BLADE-359] prefer double quotes (06605a8c0b)
- [BLADE-359] Fix Windows Update (64c73764fc)
- [BLADE-359] rename (c28bbdec41)
- [BLADE-359] wordsmith (f9bd3787b4)
- [BLADE-359] better quote handling (eb37c5d053)
- [BLADE-359] rename (bdb5c464b4)
- [BLADE-359] Use a batch file for Blade Windows Update (fe4c6fd3fa)
- [BLADE-359] Enable update functionality on Windows (a2e8281123)
- [BLADE-360] Add missing classes to jar (077f5ecb46)
- [BLADE-358] as used (031755bd24)
- [BLADE-358] put variables closest to where they are needed (136491954d)
- [BLADE-358] always write the update check (31f3fba596)
- [BLADE-358] throw an error if we can't determine blade version (f0ebf082d7)
- [BLADE-358] : if I'm on a snapshot version, the automatic 'checkForUpdate'
doesn't prompt if a new snapshot is available (0e745e1a86)
- [BLADE-357] remove duplicate classes from maven-profile (0b4051ccb8)
- [BLADE-356] Clean up lingering server process (5eec603229)

### Dependencies
- [BLADE-375] Update the gradle-base-services-groovy dependency to version
4.10.2.
- [BLADE-375] Update the gradle-core dependency to version 4.10.2.
- [BLADE-375] Update the gradle-tooling-api dependency to version 4.10.2.
- [BLADE-370] Update the com.liferay.blade.extensions.maven.profile dependency
to version latest.integration.
- [BLADE-369] Update the com.liferay.project.templates dependency to version
4.2.4.
- [BLADE-366] Update the com.liferay.project.templates dependency to version
4.2.3.
- [BLADE-356] Update the javasysmon dependency to version 0.3.5.
- [BLADE-356] Update the zt-process-killer dependency to version 1.9.

## 3.3.0 - 2018-11-15

### Commits
- [BLADE-355] sort (4ba3d0fe93)
- [BLADE-335] removed extra slashes (b921228e58)
- [BLADE-349] call assemble and then watch with no-rebuild to make watch tasks
faster (f0e16e5b75)
- [BLADE-320] rename (2e4d05cf94)
- [BLADE-320] once a day, after each command is run, check for updates.
(1308de211f)
- [BLADE-335] fix api (2380391d78)
- [BLADE-335] parse xml doc instead of html (28618bb425)
- [BLADE-335] add test (094d7efb7e)
- [BLADE-335] use correct snapshot repo URL (0bdad5ab8d)
- [BLADE-335] used error() instead of err() (a46a3ea281)
- [BLADE-335] Simplify output (3351a7414a)
- [BLADE-335] declare this variable as close to where it is actually needed as
possible. (ba01378c51)
- [BLADE-335] normalize outputs (08d4c3d676)
- [BLADE-335] keep URL related constants private so we don't overly polute the
public API (fa235191e5)
- [BLADE-335] use bnd version class (aa7fe4d25e)
- [BLADE-335] rename (ed3f3357a3)
- [BLADE-335] use safer method toURI (a2a3602813)
- [BLADE-335] use static field (ae1218dc1a)
- [BLADE-335] rename (8500af01f7)
- [BLADE-335] normalize version output (0bb491bc17)
- [BLADE-335] use static method (e3295ff0e2)
- [BLADE-335] add new logic for update (f6ebd3ee18)
- [BLADE-335] make test names consistent. Refactor two tests for new update
logic. (5948f064b1)
- [BLADE-335] make getBladeCLIVersion method static for use by UpdateCommand
(18d82bb50f)
- [BLADE-335] add default value for bladeJarPath for testing in ide (db74aea3f7)
- [BLADE-335] use private constants (569c580851)
- [BLADE-335] remove unneeded test (b17a996be8)
- [BLADE-335] simplify (edb38395cf)
- [BLADE-335] we can remove this now since we have a test class for it
(71e2179e50)
- [BLADE-335] move this 'version' test into a VersionCommandTest (e936746234)
- [BLADE-335] simplify tests (d655529727)
- [BLADE-335] instead of hard-coding "build/lib/blade.jar" pass in the
jar.archivePath from gradle (caa741a68d)
- [BLADE-335] rename test to UpdateCommandTest since it tests Updates
(93374f0fe1)
- [BLADE-335] add new update API to UpdateCommand explicitly (82cd8ca24f)
- [BLADE-335] add api to VersionCommand to get version explicitly (08cc6c7c90)
- [BLADE-335] : blade update should install updates from nexus repository
(10ef779a22)
- [BLADE-342] rename (3d21e92d30)
- [BLADE-342] Use Scanner to read the input (90541c4b7e)
- [BLADE-347] update paths (cdbba99d6d)
- [BLADE-347] not needed (64aa94a90c)
- [BLADE-347] move task configuration to afterEvaluate closure (9970f42afd)
- [BLADE-347] move generated jar/zip files to buildDir (32975e22b6)
- [BLADE-347] Better Resource Cleanup (6d3d330fe9)
- [BLADE-347] Include maven-profile in default blade cli (55b6713ece)
- [BLADE-342] provide default for prompt in case of migrating workspace
(acab346443)
- [BLADE-342] remove while loop since it isn't clear it should be necessary
(c7ac8480bd)
- [BLADE-342] not needed anymore (e6ceeac845)
- [BLADE-342] rename method (4e969d603b)
- [BLADE-342] changes requested and additional refactoring (7a42557da3)
- [BLADE-342] prompt to create settings.properties if missing in maven
(5fec2e3350)
- [BLADE-333] fix private package for tooling (802a822b42)
- [BLADE-344] additional details (d5acddea22)
- [BLADE-347] fix test (6f8e77a9f4)
- [BLADE-347] rename (1302526d79)
- [BLADE-347] Add getProperties() to CreateCommand (b1fbe95c7f)
- [LRDOCS-5843] Fix links (0efee0c979)
- [LRDOCS-5843] Final updates (24dd19234e)
- [LRDOCS-5843] Wordsmithing (6d89f83259)
- [LRDOCS-5843] Wrap 80 col and formatting (89fbce75a1)
- [BLADE-350] update gradle wrapper version for test projects to same as root
project (b331515e03)
- [BLADE-334] improve tooling.zip location handling (a289d52912)
- [BLADE-334] update to gradlew in test-resources (d321c5db9b)
- [BLADE-334] We should add set method for create command arg (1d6f407e06)
- [BLADE-346] fix tests by adding liferay CDN repo to pom. (8ac3e1a3ab)
- [BLADE-346] update project template version (ac762fc4ef)
- [BLADE-345] make path required (f21d2c5821)
- [BLADE-344] more informative and suggest --trace option (ffcbf94b88)
- [BLADE-321] normalize var names (7e324a70cc)
- [BLADE-321] Remove redundant path elements in message (593d823c77)
- [BLADE-321] Fix war deploy with versions in filename (c1df6abf04)
- [BLADE-334] We should add set method for create command arg (a21d5e93d4)
- [BLADE-333] windows paths (552f71fe98)
- [BLADE-333] refactor gradle tooling API (a08137f421)
- [BLADE-333] reorganize gradle tooling model and enable snapshot publishing to
liferay nexus (160bb64464)
- [BLADE-332] Use new Project Templates in Blade (4f88c60460)
- [BLADE-331] improve error output (7beb762afc)
- [BLADE-331] disable validation (ce61e0ece7)
- [BLADE-331] use improved BladeTest instance instead (569f825377)
- [BLADE-331] improve BladeTest error handling (53bab4755e)
- [BLADE-331] improve error handling in tests (04ad00c6f6)
- [BLADE-331] only deleteDir if exists (553cd10b0c)
- [BLADE-331] remove unused (90dbffb765)
- [BLADE-331] use deleteDir (0a19d03dbb)
- [BLADE-331] sort (3817ff0fd7)
- [BLADE-331] fix compile error (f22c0b9c26)
- [BLADE-331] combine into one class (df5a31f3f7)
- [BLADE-331] refactor to test class (3674825920)
- [BLADE-331] rename (d8bef7f30a)
- [BLADE-331] remove unneeded dependency (9548a032b3)
- [BLADE-331] sort (a034fef4c0)
- [BLADE-331] blade server init maven support (a1ef179bf6)
- [BLADE-327] now a fix for linux :) (af3adc48f8)
- [BLADE-327] better error msgs (d739be7f54)
- [BLADE-327] fix windows (4cda6f5a79)
- [BLADE-327] remove IO usage (8bdd4af4c9)
- [BLADE-327] specify precise subset of org.gradle packages and improve init
script (056da92271)
- [BLADE-313] Server Maven Support Fix (8d1e071862)
- [BLADE-313] Server Start / Stop Maven Support (0becdc0ccf)
- [BLADE-214] Maven Profile Fixes (4e8e203460)
- [BLADE-327] new watch command prototype (bd2554d20a)
- [BLADE-327] move SF setup into parent and apply SF (13b6789137)
- [BLADE-327] refactor tooling model into sub projects (d1f853f953)
- [BLADE-323] readme updates (46b7d0a258)
- [BLADE-323] improvements (79ebf3bf56)

### Dependencies
- [BLADE-335] Update the jsoup dependency to version 1.11.3.
- [BLADE-346] Update the com.liferay.project.templates dependency to version
4.1.10.
- [BLADE-332] Update the com.liferay.project.templates dependency to version
4.1.9.

## 3.2.0 - 2018-10-05

### Commits
- [BLADE-323] update version to 3.2.0 (a07cf41d99)
- [BLADE-323] initial blade extensions documentation (d5285c36e5)
- [BLADE-323] add a sample workspace template to be used with a profile
(2be99a651c)
- [BLADE-323] fix existing sample tests (943ee88edd)
- [BLADE-323] add support for setting a profile with init (fa1f5c9a7a)
- [BLADE-315] disable test on windows (023cd529b5)
- [BLADE-315] we should just show gradle task output (f4f882e4c3)
- [BLADE-304] rename (8098c6810e)
- [BLADE-304] improve version handling (6cafdf6e5d)
- [BLADE-304] set don't want to set the blade profile settings just because of
one create command choice (36bec8df66)
- [BLADE-304] turns out the name liferayVersionDefault was better (618298f5c5)
- [BLADE-304] Simplify (5e8c94c817)
- [BLADE-304] Allow for multiple BladeSettings (2c64050c37)
- [BLADE-304] Move logic out of main class (d0cc13f99f)
- [BLADE-304] Remove default from name (3c9b8683bf)
- [BLADE-304] Format Source (1df510fe5d)
- [BLADE-304] Rename variable (7a253ac459)
- [BLADE-304] Not needed (3b1a14a60a)
- [BLADE-304] Revert samples changes (9592cee443)
- [BLADE-304] Don't break backwards compatibility (03a8f54af3)
- [BLADE-304] Fix standalone case (abe1be0a6f)
- [BLADE-304] Samples command doesn't have liferayVersion arg (6dee41ea0d)
- [BLADE-304] Add Tests (44838c39d1)
- [BLADE-304] Refactor version saving (811bb9115f)
- [BLADE-304] Get default Liferay version from settings.properties (1211ea9e1c)
- [BLADE-304] Add setter and getter for default Liferay version (911bc2bfd2)
- [BLADE-304] Add getters and setters (d65644041d)
- [BLADE-304] Remove default version (167d2c0acc)
- [BLADE-304] Write default Liferay version to settings.properties (d1f0a4118f)
- [BLADE-315] Add a test (3568155c02)
- [BLADE-315] use scanner (4a73fcee78)
- [BLADE-315] wordsmith (cc673c8ba8)
- [BLADE-315] Changes requested in review (3601ab0a90)
- [BLADE-315] Fix tests (9af88f5049)
- [BLADE-315] Server Init Command (7dd73e8bd0)
- [BLADE-214] Maven Profile (3196f9185d)
- [BLADE-314] var names (8f6f0a7579)
- [BLADE-314] no need for field variables we can just use local versions
(593d612bc3)
- [BLADE-314] Fix Tests and remove statics (b6eeb13cb8)
- [BLADE-314] Add buildType test (caa265f841)
- [BLADE-314] fix constants (f85156a2b5)
- [BLADE-314] Add getter for SamplesArgs (aa645ba2c7)
- [BLADE-314] Add tests (88928e73a6)
- [BLADE-314] Pass in Liferay Version to Samples Command (a6f3872c0a)
- [BLADE-314] Add Samples Args (d2b8fd004f)
- [BLADE-309] Add test in a workspace (793cb90d96)
- [BLADE-309] @BladeProfile restricted to workspaces (37d346c8fd)
- [BLADE-311] rename and simplify (6430a2835e)
- [BLADE-311] Make sure to use parent ClassLoader (14f65de111)
- [BLADE-310] handle open parameter with no arg and add more logging
(a82ffabc4a)
- [BLADE-308] remove unused (8f65a32043)
- [BLADE-308] rename (77c610475b)
- [BLADE-308] add a method instead of accesing field directly (3879e6586e)
- [BLADE-308] rename (db3407cdf6)
- [BLADE-308] combine classloaders (c760c8959a)
- [BLADE-305] enable buildscan (cb9ee57d27)
- [BLADE-308] SF new class (d9db20897a)
- [BLADE-308] combine classloaders (40a75d04ff)
- [BLADE-307] use compileOnly but also add project to testCompile (8f43fde684)
- [BLADE-307] rename (3db73effa1)
- [BLADE-307] Requested changes and SF (0378043b1d)
- [BLADE-307] Refactor extension tests (259ce0526e)
- [BLADE-286] change argument to -m -M options and update test so only -m is
required (4f107a1411)
- [BLADE-286] create module ext project command (fdff6337c1)
- [BLADE-301] Add set methods for Server related args (d7aae32c8e)
- [BLADE-300] add setter methods for InitArgs (56fda51e50)
- [BLADE-296] normalize output (20f49e0502)
- [BLADE-302] for GradleWrapper command we should not capture IO (108a212a21)
- [BLADE-296] Print Deploy Output (a1290d3743)
- [BLADE-294] fix open option handling (6cd254b361)
- [BLADE-274] Support updating and starting wars correctly from blade
(f966db3ada)
- [BLADE-214] fix types (bf0c8e214f)
- [BLADE-288] update create script to use 7.1 project templates (9b3d6ec89a)
- [BLADE-284] update to project.templates 4.1.6 (80756b41cf)
- [BLADE-277] improve test (5b0fd96ac0)
- [BLADE-277] remove autoCloseable, not needed (6a348fcc3d)
- [BLADE-281] fix initCommandTest can't compiled (76c3e1d596)
- [BLADE-281] fix liferay-workspace-plugin version (1e1d98cdd4)
- [BLADE-276] Fix blade init . (9acc0ba442)
- [BLADE-281] update variable names and rename test methods (156ae84a65)
- [BLADE-281] simplify (b192a283de)
- [BLADE-281] simplify temp folders and rename files (78b60f3df4)
- [BLADE-281] extract new WorkspaceUtil class (64b43f3861)
- [BLADE-281] simplify pattern (231dff9b25)
- [BLADE-281] use OSGi Version class instead of Maven ComparableVersion
(88dca11598)
- [BLADE-281] rename variables (430aa4815c)
- [BLADE-281] rename method (56b3f376f9)
- [BLADE-281] support target platform for blade (d999f5e480)
- [BLADE-280] add our nexus cdn to poms since not all artifacts are being synced
to central (4f78fd1b31)
- [BLADE-280] update dependency to 4.1.5 (2b24fde06d)

### Dependencies
- [BLADE-286] Update the com.liferay.project.templates dependency to version
4.1.7.
- [BLADE-284] Update the com.liferay.project.templates dependency to version
4.1.6.
- [BLADE-277] Update the zt-process-killer dependency to version 1.8.
- [BLADE-280] Update the com.liferay.project.templates dependency to version
4.1.5.

## 3.1.2 - 2018-08-10

### Commits
- [BLADE-214] fix test (d0732984c9)
- [BLADE-214] update test (7d2f0177bb)
- [BLADE-214] refator profile sample to 'new' and 'overridden' command
(4f143ba620)
- [BLADE-214] Blade Profile Extension Example, test, and SF (4ed22e8ac0)
- [BLADE-277] fix tests (3f26f113f3)
- [BLADE-277] avoid wrapping and name variables based on complex type
(552f23be8d)
- [BLADE-277] refactor to new ServerUtil class (491a677256)
- [BLADE-277] Refactor to get reference to process (84e16c6fdd)
- [BLADE-277] Test real commands (57049ab0d5)
- [BLADE-277] Fix blade server stop and add tests (a0c68acb58)
- [BLADE-276] Handle current directory better (d40ba3548f)
- [BLADE-271] rename (56128cc5b2)
- [BLADE-271] reduce api (eb2ad4fac7)
- [BLADE-271] Better identification of server folders (ccffc9e988)
- [BLADE-265] Fix Tests (e6a56e8a74)
- [BLADE-265] rebased (1211e094d9)
- [BLADE-265] Print Gradle Errors in Test Results (5db8a78b7d)
- [BLADE-270] fix compile error (287fafe320)
- [BLADE-270] reword (5ba05ae3bc)
- [BLADE-270] rename (ae7109a82c)
- [BLADE-270] use lamda (b3d03d96bc)
- [BLADE-270] rename (d41757997b)
- [BLADE-270] rename (d08fb7cf02)
- [BLADE-270] Fix init and treat --base correctly (a419906bd9)
- [BLADE-256] remove mocks since we can now perform a real uninstall
(3df42b8836)
- [BLADE-256] refator BladeTest to have a parameter for userHomeDir that comes
from junit temporary Folder rule (3a650e54a9)
- [BLADE-256] refactor extension path to come from blade settings class instead
of having a mutable userHomeDir on the Blade(Test) class. (6efb3c72f8)
- [BLADE-256] rename (9823a07711)
- [BLADE-256] : Run tests in parallel (99723bdf01)
- [BLADE-266] clean out existing mavenRepo (44c6c89e14)
- [BLADE-262] switch initArgs to 7.1 default (af8337cf9e)
- [BLADE-262] update to project template 4.1.1 (5bc60eccab)
- [BLADE-264] Fix tests (26ab09a9fe)
- [BLADE-264] Update to Liferay Portal 7.1 (a9e9817f6f)
- [BLADE-260] Handle Stream properly in in ServerStopCommand (d505a071ff)
- [BLADE-259] rename (432dfe5191)
- [BLADE-259] Fix NPE in ServerStopCommand (de1de0fb9a)
- [BLADE-257] rename and SF (47b2985358)
- [BLADE-257] Requested Changes (f7c82afc28)
- [BLADE-257] Recognize commands with spaces (9e4b5383cc)

### Dependencies
- [BLADE-262] Update the com.liferay.project.templates dependency to version
4.1.1.

## 3.1.1 - 2018-07-16

### Commits
- [BLADE-253] update to 3.1.1 (5f659fd1e5)

## 3.1.0 - 2018-07-16

### Commits
- [BLADE-253] specify version in gradle config (1ee2f9a03c)
- [BLADE-258] update test (10f7260c07)
- [BLADE-258] always print out errors don't require trace argument (cc9a81f143)
- [BLADE-258] simplify (ba0851989b)
- [BLADE-258] reword (91821d2a28)
- [BLADE-258] Print blade deploy errors and honor --trace (9e6259b0ee)
- [BLADE-211] configure changelog for BLADE project (b8f4b69004)
- [BLADE-211] Apply changelog plugin (e192de3f58)
- [BLADE-250] Interactive (77d4dfceb8)
- [BLADE-251] Fix BladeUtil.findParentFile (a7711db76a)
- [BLADE-250] Extensions Uninstall Fix Windows (0c6f43a11d)
- [BLADE-250] Extension installed twice (5bc6a65b88)
- [BLADE-250] failing test case (700dcfdab9)
- [BLADE-231] refator to new class BladeSettings and use it to setup extensions
(c4d423b3f5)
- [BLADE-231] sort gradle file (4b2bdb5205)
- [BLADE-231] first time to use IntStream :) (276116febe)
- [BLADE-231] Profile Work (67e3cad67c)
- [BLADE-231] dont depend on internal class make our own copy (189c448e9e)
- [BLADE-231] more specific (0a73d25ff0)
- [BLADE-231] not needed (f588c9de05)
- [BLADE-231] sort (89fb65380c)
- [BLADE-231] Add github extension download test (95053de2c5)
- [BLADE-231] Fix gradle build (8706745be8)
- [BLADE-231] fix searching for windows (8cb92c2d4f)
- [BLADE-231] remove unneeded file (f5530a8534)
- [BLADE-231] switch to zip file (dac315de4a)
- [BLADE-231] reorganize logic into isExtension which supports both templates
and commands (83d78dfea6)
- [BLADE-231] reduce API (d1af533613)
- [BLADE-231] remove mocks and test real sample command (97df7fd1df)
- [BLADE-231] Fix test (0356beb9b5)
- [BLADE-231] Format Source (4139070ac3)
- [BLADE-231] Expand Custom Project Template Support (51c9743a1c)
- [BLADE-214] formatting (8f498cdd47)
- [BLADE-214] Sample Command Eclipse Compile fix (5f68fd5dd9)
- [BLADE-214] Deploy Test Mock Fix (01834b9582)
- [BLADE-214] add sample template (b72ca0ef96)
- [BLADE-214] test fixes (6e50e2826b)
- [BLADE-214] refactored into multi-module project (9739f5cb83)
- [BLADE-214] refactor commands to new package (0955e5062e)
- [BLADE-214] extract to new Extensions class (d57dd7a2e5)
- [BLADE-214] always use _ instead of this (b48fcfcf42)
- [BLADE-214] Update template commands after rebase (686850b0ef)
- [BLADE-214] fix tests (032ddfd77e)
- [BLADE-214] Custom Extension Support (7eb1f7eaeb)
- [BLADE-231] fix test (af3021dabd)
- [BLADE-231] add back (128dc4d7f2)
- [BLADE-231] cleanup uninstall template (37d78ca730)
- [BLADE-231] cleanup install template command (7d70dd92bf)
- [BLADE-231] remove list template command (15988c424e)
- [BLADE-231] Blade custom project template support (d2ae3f96b9)
- [BLADE-246] reformat output message and add test (9780f996c2)
- [BLADE-246] fix test (3b9f0a8f36)
- [BLADE-246] Print warning when service template missing -s flag (e34e7a8259)
- [BLADE-244] use bright green for STOPPED (cebe9341a8)
- [BLADE-244] use patterns (7d5e222fed)
- [BLADE-244] add colors to blade server start (be10d2e78f)
- [BLADE-235] add more tests (5d01f1640c)
- [BLADE-235] junit test for init with liferay version (0704f92bbd)
- [BLADE-235] add liferay version support for blade init command (05a38ecee7)
- [BLADE-233] don't chain (97e376315f)
- [BLADE-233] fix test (f7c0dbea2f)
- [BLADE-233] better blade deploy message (9824459992)
- [BLADE-233] blade deploy causes error (1ef07e0aa5)
- [BLADE-228] fix test (c93f7eb2a9)
- [BLADE-228] remove maven local (1bc98d6b48)
- [BLADE-228] update test (427b69b688)
- [BLADE-228] don't need this (dda746c6d9)
- [BLADE-228] blade create should support specifying the Liferay Version
(5a4e1cae05)
- [BLADE-228] Add mavenLocal as explicit repository (bad768580c)
- [BLADE-228] Add JCommander as explicit dependency (d1c90ec5ae)
- [BLADE-230] make more friendly (88d9263e76)
- [BLADE-230] use new constructor to pass printStreams (c15f5e1870)
- [BLADE-230] Fix ordering (7ecd40afa8)
- [BLADE-230] Changes requested (06b48b3292)
- [BLADE-230] blade -t should be required (a93927cfcd)
- [BLADE-227] formatting (ee3652e57f)
- [BLADE-227] Make general help less verbose (bc03e5c972)
- [BLADE-226] rename (25efe8effe)
- [BLADE-226] Blade creation of fragment requires -h and -H (203bc2b080)
- [BLADE-219] formatting (6d54088226)
- [BLADE-219] Fix Server Start Command (a05f90df29)
- [BLADE-216] review and formatting (33b8b2b378)
- [BLADE-190] : Fix Deploy Tests (9b14dbe1f2)
- [BLADE-190] : Fix some issues with deploy (debc2f850a)
- [BLADE-190] : Refactor code and tests (377fadddd6)
- [BLADE-190] rename (968f5fb370)
- [BLADE-190] update deploy test with example jar and war files (9488fb4ddb)
- [BLADE-190] refactor deploy to use gogotelnetclient and domain classes
(3aac877678)
- [BLADE-190] : blade to support deploying wars to Liferay (3e5d7e1595)
- [BLADE-209] : Restore Help Command (b7a8efd0e5)
- [BLADE-212] fix blade init command failed for no destination (1e41cef115)
- [BLADE-208] review (59de71b72c)
- [BLADE-208] : Restore Version Command (f69cf16719)
- [BLADE-199] reword and source format (d8d695eb6a)
- [BLADE-199] add new test for gradle build option (07acd77402)
- [BLADE-199] add creating maven workspace support (437eeee06a)
- [BLADE-210] : Fix Project Creation Path (2de6002a8e)
- [BLADE-210] : Fix Project Creation Path (79ef9915f9)
- [BLADE-206] make project specific (8325bacabc)
- [BLADE-206] : Wars in wars folder (01f7a0ede9)
- [BLADE-202] : Use JCommander (b08d285090)

### Dependencies
- [BLADE-211] Update the com.liferay.gradle.plugins.change.log.builder
dependency to version 1.1.0.
- [] Update the diffutils dependency to version 1.3.0.
- [] Update the zipdiff dependency to version 1.0.
- [] Update the com.liferay.project.templates dependency to version 4.1.0.
- [] Update the com.liferay.project.templates dependency to version 4.0.1.
- [] Update the powermock-classloading-xstream dependency to version 1.7.3.
- [] Update the powermock-module-junit4-rule dependency to version 1.7.3.
- [BLADE-214] Update the asciitable dependency to version 0.3.2.
- [] Update the com.liferay.gogo.shell.client dependency to version 1.0.0.
- [BLADE-244] Update the jansi dependency to version 1.17.1.
- [BLADE-228] Update the com.liferay.project.templates dependency to version
4.0.0.
- [BLADE-190] Update the easymock dependency to version 3.5.1.
- [BLADE-190] Update the powermock-api-easymock dependency to version 1.7.3.
- [BLADE-190] Update the powermock-module-junit4 dependency to version 1.7.3.
- [BLADE-190] Update the osgi.core dependency to version 6.0.0.
- [] Update the gradle-core dependency to version 3.0.
- [] Update the com.liferay.project.templates dependency to version 3.1.3.
- [] Update the com.liferay.project.templates dependency to version 3.1.1.

## 2.1.0 - 2017-06-14

### Commits
- [LRDOCS-3688] README should now point to official docs for easier
maintainability (7f9bc3f45e)
- [BLADE-151] bump to 2.1.1 (c8c6805d4d)
- [BLADE-151] remove reference to older sdk file (dc6a4e7243)
- [BLADE-151] switch to 1.0.9 (e7bfba6d7b)
- [IDE-3167] avoid checking content empty file (aa20ee9740)

## 2.0.2 - 2017-04-04

### Commits
- [LPS-50156] and LPS-54798 (49d4bd764c)

## 2.0.0 - 2017-01-13

### Commits
- [IDE-3043] fix infinitely pluings-sdk dir copying (3ecda4607b)
- [LPS-58672] ) (d4e6b36544)
- [LPS-47580] (Moved Recycle Bin Logic Into a New DLTrashService Interface)
(818a2fb19c)

## 1.2.0 - 2016-10-14

### Commits
- [LPS-50156] problem (dc18d4431e)
- [LPS-67504] add tests (512a3208b8)
- [LPS-67504] Update version for template bundle that has api (b4a1085218)
- [LPS-65477] update to gradle templates 1.0.18 that contains bug fix
(d068cfb6aa)
- [LPS-65477] add tests (385cedb3ac)
- [LPS-65477] add new blade templates (582f305e00)

## 1.0.1 - 2016-07-20

### Commits
- [IDE-2756] fix fragment deploying error (cc949d5a97)

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
[BLADE-488]: https://issues.liferay.com/browse/BLADE-488
[BLADE-489]: https://issues.liferay.com/browse/BLADE-489
[BLADE-490]: https://issues.liferay.com/browse/BLADE-490
[BLADE-493]: https://issues.liferay.com/browse/BLADE-493
[BLADE-496]: https://issues.liferay.com/browse/BLADE-496
[BLADE-499]: https://issues.liferay.com/browse/BLADE-499
[BLADE-500]: https://issues.liferay.com/browse/BLADE-500
[BLADE-501]: https://issues.liferay.com/browse/BLADE-501
[BLADE-502]: https://issues.liferay.com/browse/BLADE-502
[BLADE-503]: https://issues.liferay.com/browse/BLADE-503
[BLADE-504]: https://issues.liferay.com/browse/BLADE-504
[BLADE-505]: https://issues.liferay.com/browse/BLADE-505
[BLADE-507]: https://issues.liferay.com/browse/BLADE-507
[BLADE-508]: https://issues.liferay.com/browse/BLADE-508
[BLADE-509]: https://issues.liferay.com/browse/BLADE-509
[BLADE-510]: https://issues.liferay.com/browse/BLADE-510
[BLADE-513]: https://issues.liferay.com/browse/BLADE-513
[BLADE-515]: https://issues.liferay.com/browse/BLADE-515
[BLADE-517]: https://issues.liferay.com/browse/BLADE-517
[BLADE-518]: https://issues.liferay.com/browse/BLADE-518
[BLADE-519]: https://issues.liferay.com/browse/BLADE-519
[BLADE-520]: https://issues.liferay.com/browse/BLADE-520
[BLADE-522]: https://issues.liferay.com/browse/BLADE-522
[BLADE-532]: https://issues.liferay.com/browse/BLADE-532
[BLADE-533]: https://issues.liferay.com/browse/BLADE-533
[BLADE-534]: https://issues.liferay.com/browse/BLADE-534
[BLADE-535]: https://issues.liferay.com/browse/BLADE-535
[BLADE-537]: https://issues.liferay.com/browse/BLADE-537
[BLADE-538]: https://issues.liferay.com/browse/BLADE-538
[BLADE-539]: https://issues.liferay.com/browse/BLADE-539
[BLADE-542]: https://issues.liferay.com/browse/BLADE-542
[BLADE-543]: https://issues.liferay.com/browse/BLADE-543
[BLADE-544]: https://issues.liferay.com/browse/BLADE-544
[BLADE-545]: https://issues.liferay.com/browse/BLADE-545
[BLADE-547]: https://issues.liferay.com/browse/BLADE-547
[BLADE-548]: https://issues.liferay.com/browse/BLADE-548
[BLADE-549]: https://issues.liferay.com/browse/BLADE-549
[BLADE-550]: https://issues.liferay.com/browse/BLADE-550
[BLADE-552]: https://issues.liferay.com/browse/BLADE-552
[BLADE-553]: https://issues.liferay.com/browse/BLADE-553
[BLADE-554]: https://issues.liferay.com/browse/BLADE-554
[BLADE-555]: https://issues.liferay.com/browse/BLADE-555
[BLADE-557]: https://issues.liferay.com/browse/BLADE-557
[BLADE-558]: https://issues.liferay.com/browse/BLADE-558
[BLADE-563]: https://issues.liferay.com/browse/BLADE-563
[BLADE-564]: https://issues.liferay.com/browse/BLADE-564
[BLADE-565]: https://issues.liferay.com/browse/BLADE-565
[BLADE-566]: https://issues.liferay.com/browse/BLADE-566
[BLADE-567]: https://issues.liferay.com/browse/BLADE-567
[BLADE-568]: https://issues.liferay.com/browse/BLADE-568
[BLADE-570]: https://issues.liferay.com/browse/BLADE-570
[BLADE-572]: https://issues.liferay.com/browse/BLADE-572
[BLADE-573]: https://issues.liferay.com/browse/BLADE-573
[BLADE-576]: https://issues.liferay.com/browse/BLADE-576
[BLADE-577]: https://issues.liferay.com/browse/BLADE-577
[BLADE-578]: https://issues.liferay.com/browse/BLADE-578
[BLADE-579]: https://issues.liferay.com/browse/BLADE-579
[BLADE-580]: https://issues.liferay.com/browse/BLADE-580
[BLADE-582]: https://issues.liferay.com/browse/BLADE-582
[BLADE-585]: https://issues.liferay.com/browse/BLADE-585
[BLADE-587]: https://issues.liferay.com/browse/BLADE-587
[BLADE-588]: https://issues.liferay.com/browse/BLADE-588
[BLADE-589]: https://issues.liferay.com/browse/BLADE-589
[BLADE-591]: https://issues.liferay.com/browse/BLADE-591
[BLADE-592]: https://issues.liferay.com/browse/BLADE-592
[BLADE-593]: https://issues.liferay.com/browse/BLADE-593
[BLADE-594]: https://issues.liferay.com/browse/BLADE-594
[BLADE-595]: https://issues.liferay.com/browse/BLADE-595
[BLADE-596]: https://issues.liferay.com/browse/BLADE-596
[BLADE-597]: https://issues.liferay.com/browse/BLADE-597
[BLADE-598]: https://issues.liferay.com/browse/BLADE-598
[BLADE-599]: https://issues.liferay.com/browse/BLADE-599
[BLADE-600]: https://issues.liferay.com/browse/BLADE-600
[BLADE-603]: https://issues.liferay.com/browse/BLADE-603
[BLADE-604]: https://issues.liferay.com/browse/BLADE-604
[BLADE-605]: https://issues.liferay.com/browse/BLADE-605
[BLADE-607]: https://issues.liferay.com/browse/BLADE-607
[BLADE-611]: https://issues.liferay.com/browse/BLADE-611
[BLADE-617]: https://issues.liferay.com/browse/BLADE-617
[BLADE-618]: https://issues.liferay.com/browse/BLADE-618
[BLADE-620]: https://issues.liferay.com/browse/BLADE-620
[BLADE-621]: https://issues.liferay.com/browse/BLADE-621
[BLADE-622]: https://issues.liferay.com/browse/BLADE-622
[BLADE-627]: https://issues.liferay.com/browse/BLADE-627
[BLADE-628]: https://issues.liferay.com/browse/BLADE-628
[BLADE-629]: https://issues.liferay.com/browse/BLADE-629
[BLADE-630]: https://issues.liferay.com/browse/BLADE-630
[BLADE-631]: https://issues.liferay.com/browse/BLADE-631
[BLADE-632]: https://issues.liferay.com/browse/BLADE-632
[BLADE-633]: https://issues.liferay.com/browse/BLADE-633
[BLADE-635]: https://issues.liferay.com/browse/BLADE-635
[BLADE-637]: https://issues.liferay.com/browse/BLADE-637
[BLADE-639]: https://issues.liferay.com/browse/BLADE-639
[BLADE-640]: https://issues.liferay.com/browse/BLADE-640
[BLADE-643]: https://issues.liferay.com/browse/BLADE-643
[BLADE-644]: https://issues.liferay.com/browse/BLADE-644
[BLADE-646]: https://issues.liferay.com/browse/BLADE-646
[BLADE-649]: https://issues.liferay.com/browse/BLADE-649
[BLADE-651]: https://issues.liferay.com/browse/BLADE-651
[BLADE-655]: https://issues.liferay.com/browse/BLADE-655
[BLADE-657]: https://issues.liferay.com/browse/BLADE-657
[BLADE-659]: https://issues.liferay.com/browse/BLADE-659
[BLADE-660]: https://issues.liferay.com/browse/BLADE-660
[BLADE-663]: https://issues.liferay.com/browse/BLADE-663
[BLADE-664]: https://issues.liferay.com/browse/BLADE-664
[BLADE-668]: https://issues.liferay.com/browse/BLADE-668
[BLADE-672]: https://issues.liferay.com/browse/BLADE-672
[BLADE-673]: https://issues.liferay.com/browse/BLADE-673
[BLADE-674]: https://issues.liferay.com/browse/BLADE-674
[BLADE-675]: https://issues.liferay.com/browse/BLADE-675
[BLADE-677]: https://issues.liferay.com/browse/BLADE-677
[BLADE-678]: https://issues.liferay.com/browse/BLADE-678
[BLADE-685]: https://issues.liferay.com/browse/BLADE-685
[BLADE-687]: https://issues.liferay.com/browse/BLADE-687
[BLADE-688]: https://issues.liferay.com/browse/BLADE-688
[BLADE-690]: https://issues.liferay.com/browse/BLADE-690
[BLADE-691]: https://issues.liferay.com/browse/BLADE-691
[BLADE-692]: https://issues.liferay.com/browse/BLADE-692
[BLADE-694]: https://issues.liferay.com/browse/BLADE-694
[BLADE-695]: https://issues.liferay.com/browse/BLADE-695
[BLADE-696]: https://issues.liferay.com/browse/BLADE-696
[BLADE-697]: https://issues.liferay.com/browse/BLADE-697
[BLADE-698]: https://issues.liferay.com/browse/BLADE-698
[BLADE-699]: https://issues.liferay.com/browse/BLADE-699
[BLADE-700]: https://issues.liferay.com/browse/BLADE-700
[BLADE-701]: https://issues.liferay.com/browse/BLADE-701
[BLADE-702]: https://issues.liferay.com/browse/BLADE-702
[BLADE-703]: https://issues.liferay.com/browse/BLADE-703
[BLADE-705]: https://issues.liferay.com/browse/BLADE-705
[BLADE-707]: https://issues.liferay.com/browse/BLADE-707
[BLADE-708]: https://issues.liferay.com/browse/BLADE-708
[BLADE-709]: https://issues.liferay.com/browse/BLADE-709
[BLADE-710]: https://issues.liferay.com/browse/BLADE-710
[BLADE-711]: https://issues.liferay.com/browse/BLADE-711
[BLADE-712]: https://issues.liferay.com/browse/BLADE-712
[BLADE-713]: https://issues.liferay.com/browse/BLADE-713
[BLADE-714]: https://issues.liferay.com/browse/BLADE-714
[BLADE-716]: https://issues.liferay.com/browse/BLADE-716
[BLADE-717]: https://issues.liferay.com/browse/BLADE-717
[BLADE-719]: https://issues.liferay.com/browse/BLADE-719
[BLADE-724]: https://issues.liferay.com/browse/BLADE-724
[BLADE-725]: https://issues.liferay.com/browse/BLADE-725
[BLADE-728]: https://issues.liferay.com/browse/BLADE-728
[BLADE-729]: https://issues.liferay.com/browse/BLADE-729
[BLADE-730]: https://issues.liferay.com/browse/BLADE-730
[BLADE-735]: https://issues.liferay.com/browse/BLADE-735
[BLADE-737]: https://issues.liferay.com/browse/BLADE-737
[BLADE-738]: https://issues.liferay.com/browse/BLADE-738
[BLADE-739]: https://issues.liferay.com/browse/BLADE-739
[BLADE-740]: https://issues.liferay.com/browse/BLADE-740
[BLADE-741]: https://issues.liferay.com/browse/BLADE-741
[BLADE-743]: https://issues.liferay.com/browse/BLADE-743
[BLADE-744]: https://issues.liferay.com/browse/BLADE-744
[BLADE-745]: https://issues.liferay.com/browse/BLADE-745
[BLADE-750]: https://issues.liferay.com/browse/BLADE-750
[IDE-2756]: https://issues.liferay.com/browse/IDE-2756
[IDE-3043]: https://issues.liferay.com/browse/IDE-3043
[IDE-3167]: https://issues.liferay.com/browse/IDE-3167
[IDE-4932]: https://issues.liferay.com/browse/IDE-4932
[LCD-14267]: https://issues.liferay.com/browse/LCD-14267
[LCD-14295]: https://issues.liferay.com/browse/LCD-14295
[LCD-14300]: https://issues.liferay.com/browse/LCD-14300
[LPD-28093]: https://issues.liferay.com/browse/LPD-28093
[LPD-28297]: https://issues.liferay.com/browse/LPD-28297
[LPD-31577]: https://issues.liferay.com/browse/LPD-31577
[LPD-32144]: https://issues.liferay.com/browse/LPD-32144
[LPD-32781]: https://issues.liferay.com/browse/LPD-32781
[LPD-41339]: https://issues.liferay.com/browse/LPD-41339
[LPS-47580]: https://issues.liferay.com/browse/LPS-47580
[LPS-50156]: https://issues.liferay.com/browse/LPS-50156
[LPS-58672]: https://issues.liferay.com/browse/LPS-58672
[LPS-65477]: https://issues.liferay.com/browse/LPS-65477
[LPS-67504]: https://issues.liferay.com/browse/LPS-67504
[LPS-98820]: https://issues.liferay.com/browse/LPS-98820
[LPS-105502]: https://issues.liferay.com/browse/LPS-105502
[LPS-105747]: https://issues.liferay.com/browse/LPS-105747
[LPS-105873]: https://issues.liferay.com/browse/LPS-105873
[LPS-106787]: https://issues.liferay.com/browse/LPS-106787
[LPS-108630]: https://issues.liferay.com/browse/LPS-108630
[LPS-110131]: https://issues.liferay.com/browse/LPS-110131
[LPS-111461]: https://issues.liferay.com/browse/LPS-111461
[LPS-112511]: https://issues.liferay.com/browse/LPS-112511
[LPS-114088]: https://issues.liferay.com/browse/LPS-114088
[LPS-114169]: https://issues.liferay.com/browse/LPS-114169
[LPS-114909]: https://issues.liferay.com/browse/LPS-114909
[LPS-119853]: https://issues.liferay.com/browse/LPS-119853
[LPS-120193]: https://issues.liferay.com/browse/LPS-120193
[LPS-120734]: https://issues.liferay.com/browse/LPS-120734
[LPS-120852]: https://issues.liferay.com/browse/LPS-120852
[LPS-122967]: https://issues.liferay.com/browse/LPS-122967
[LPS-125495]: https://issues.liferay.com/browse/LPS-125495
[LPS-128040]: https://issues.liferay.com/browse/LPS-128040
[LPS-130023]: https://issues.liferay.com/browse/LPS-130023
[LPS-130802]: https://issues.liferay.com/browse/LPS-130802
[LPS-130908]: https://issues.liferay.com/browse/LPS-130908
[LPS-131071]: https://issues.liferay.com/browse/LPS-131071
[LPS-131920]: https://issues.liferay.com/browse/LPS-131920
[LPS-133530]: https://issues.liferay.com/browse/LPS-133530
[LPS-133987]: https://issues.liferay.com/browse/LPS-133987
[LPS-134232]: https://issues.liferay.com/browse/LPS-134232
[LPS-134964]: https://issues.liferay.com/browse/LPS-134964
[LPS-137135]: https://issues.liferay.com/browse/LPS-137135
[LPS-141989]: https://issues.liferay.com/browse/LPS-141989
[LRDOCS-3688]: https://issues.liferay.com/browse/LRDOCS-3688
[LRDOCS-5843]: https://issues.liferay.com/browse/LRDOCS-5843
[LRDOCS-7448]: https://issues.liferay.com/browse/LRDOCS-7448