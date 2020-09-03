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

./gradlew --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon :extensions:project-templates-js-theme:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon :extensions:project-templates-js-widget:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon -PmavenLocal --continue clean check smokeTests --scan

checkError