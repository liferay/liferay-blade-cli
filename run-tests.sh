#!/bin/bash

function archiveTests {
	find . -name index.html | grep build/reports/tests | sed 's/\/test\/index\.html//' | xargs zip -o tests.zip -r
	find . -name "create*" | grep cli/build | xargs zip -ou tests.zip -r
}

function checkError {
	local retcode=$?

	if [ $retcode -ne 0 ]; then
		archiveTests

		exit $retcode
	fi
}

# Switch gradle wrapper distributionUrl to use -bin instead of -all. See BLADE-594 for more details

sed "s/all/bin/" gradle/wrapper/gradle-wrapper.properties > gradle-wrapper.properties.edited
mv gradle-wrapper.properties.edited gradle/wrapper/gradle-wrapper.properties

project_names=(
	maven-profile
	project-templates-js-theme
	project-templates-js-widget
	project-templates-npm-angular-portlet
	project-templates-social-bookmark
)

for projectName in "${project_names[@]}" ; do
	./gradlew --no-daemon :extensions:${projectName}:publishToMavenLocal \
		-x :cli:bladeExtensionsVersions \
		-x :cli:processResources \
		--scan

	checkError
done


./gradlew --no-daemon -PmavenLocal -Pparallel --continue clean check smokeTests --scan --stacktrace

checkError
