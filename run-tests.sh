#!/bin/bash

function archiveTests {
	find . -name index.html | grep build/reports/tests | sed 's/\/test\/index\.html//' | xargs zip -o tests.zip -r
	find . -name "create*" | grep cli/build | xargs zip -our tests.zip
}

function checkError {
	local retcode=$?

	if [ $retcode -ne 0 ]; then
		archiveTests

		exit $retcode
	fi
}

./gradlew --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon :extensions:project-templates-js-theme:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon :extensions:project-templates-js-widget:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan

checkError

./gradlew --no-daemon -PmavenLocal --continue clean check smokeTests --scan

checkError