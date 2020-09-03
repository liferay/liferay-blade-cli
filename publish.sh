#!/bin/bash

bladeInstall=$(curl -L https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/cli/installers/local | sh)

if [ "$?" != "0" ]; then
	echo $bladeInstall
	echo blade install failed
	exit 1
fi

export PATH="$PATH:$HOME/jpm/bin"

bladeVersion=$(blade version)

if [ "$?" != "0" ]; then
	echo blade version failed
	exit 1
fi

bladeTask="check"
nexusOpt=""
releaseType=""
scanOpt="--scan"

# check the arguments first
while [ $# -gt 0 ]; do
	if [ "$1" = "--local" ]; then
		nexusOpt="-PlocalNexus"
	elif [ "$1" = "--remote" ]; then
		nexusOpt="-PremoteNexus"
	elif [ "$1" = "--skip-tests" ]; then
		bladeTask="jar"
	elif [ "$1" = "--skip-scan" ]; then
		scanOpt=""
	fi
	shift
done

ISRELEASE=$(cat cli/build.gradle | grep -e "^version = \"[0-9.]*\"$")
ISSNAPSHOT=$(cat cli/build.gradle | grep -e "^version = \"[0-9.]*-SNAPSHOT\"$")

if [ ! -z "$ISRELEASE" ]; then
	releaseType="release"
fi

if [ ! -z "$ISSNAPSHOT" ]; then
	releaseType="snapshots"
fi

# Setup a temp directory
timestamp=$(date +%s)
tmpDir="/tmp/$timestamp/"

mkdir -p $tmpDir

if [ -z "$repoHost" ]; then
	if [ "$nexusOpt" = "-PlocalNexus" ]; then
		repoHost="http://localhost:8081"
	else
		repoHost="https://repository.liferay.com"
	fi
elif [ "$repoHost" = "http://localhost:8081" ]; then
	nexusOpt="-PlocalNexus"
fi

# Switch gradle wrapper distributionUrl to use -bin instead of -all. See BLADE-594 for more details

sed "s/all/bin/" gradle/wrapper/gradle-wrapper.properties > gradle-wrapper.properties.edited
mv gradle-wrapper.properties.edited gradle/wrapper/gradle-wrapper.properties

# First clean local build folder to try to minimize variants

./gradlew -q --no-daemon --console=plain clean

if [ "$?" != "0" ]; then
	echo Failed clean.
	exit 1
fi

# Publish the Remote Deploy Command jar

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:remote-deploy-command:publish --info ${scanOpt} > /tmp/$timestamp/remote-deploy-publish-command.txt; retcode=$?
remoteDeployCommandPublishCommand=$(cat /tmp/$timestamp/remote-deploy-publish-command.txt)

if [ "$retcode" != "0" ] || [ -z "$remoteDeployCommandPublishCommand" ]; then
	echo Failed :extensions:remote-deploy-command:publish
	exit 1
fi

# Publish the Activator Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-activator:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}; retcode=$?

# Publish the Content Targeting Report Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-content-targeting-report:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}; retcode=$?

# Publish the Content Targeting Rule Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-content-targeting-rule:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}; retcode=$?

# Publish the Content Targeting Tracking Action Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-content-targeting-tracking-action:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}; retcode=$?

# Publish the Freemarker Portlet Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-freemarker-portlet:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}; retcode=$?

# Publish the Social Bookmark Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-social-bookmark:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt}

# Publish the JS Theme Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-js-theme:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt} > /tmp/$timestamp/js-theme-template-publish-command.txt; retcode=$?
jsThemeTemplatePublishCommand=$(cat /tmp/$timestamp/js-theme-template-publish-command.txt)

if [ "$retcode" != "0" ] || [ -z "$jsThemeTemplatePublishCommand" ]; then
	echo Failed :extensions:project-templates-js-theme:publish
	exit 1
fi

# Publish the JS Widget Project Template
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:project-templates-js-widget:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt} > /tmp/$timestamp/js-widget-template-publish-command.txt; retcode=$?
jsWidgetTemplatePublishCommand=$(cat /tmp/$timestamp/js-widget-template-publish-command.txt)

if [ "$retcode" != "0" ] || [ -z "$jsWidgetTemplatePublishCommand" ]; then
	echo Failed :extensions:project-templates-js-widget:publish
	exit 1
fi

# Publish the Maven Profile jar
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:maven-profile:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt} > /tmp/$timestamp/maven-profile-publish-command.txt; retcode=$?
mavenProfilePublishCommand=$(cat /tmp/$timestamp/maven-profile-publish-command.txt)

if [ "$retcode" != "0" ] || [ -z "$mavenProfilePublishCommand" ]; then
	echo Failed :extensions:maven-profile:publish
	exit 1
fi

# Grep the output of the previous command to find the url of the published jar
mavenProfilePublishUrl=$(echo "$mavenProfilePublishCommand" | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f4)

if [ "$?" != "0" ] || [ -z "$mavenProfilePublishUrl" ]; then
	echo Failed grepping for mavenProfilePublishUrl
	exit 1
fi

# Download the just published jar in order to later compare it to the embedded maven profile that is in blade jar
mavenProfileJarUrl="${repoHost}${mavenProfilePublishUrl}"

curl -s "$mavenProfileJarUrl" -o /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
	echo Downloading maven.profile jar failed.
	exit 1
else
	echo "Published $mavenProfileJarUrl"
fi

# fix permissions

mkdir -p ~/.config/configstore
chmod g+rwx ~/.config ~/.config/configstore

# Test the blade cli jar locally, but don't publish.

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} --refresh-dependencies clean $bladeTask --info ${scanOpt} > /tmp/$timestamp/blade-cli-jar-command.txt; retcode=$?

if [ "$retcode" != "0" ]; then
	echo Failed :cli:jar
	cat /tmp/$timestamp/blade-cli-jar-command.txt
	exit 1
fi

# now that we have the blade jar just built, lets extract the embedded maven profile jar and compare to the maven profile downloaded from nexus

embeddedMavenProfileJar=$(jar -tf cli/build/libs/blade.jar | grep "maven.profile-")

if [ -z "$embeddedMavenProfileJar" ]; then
	echo Failed to find embedded maven.profile jar in blade jar
	exit 1
fi

unzip -p cli/build/libs/blade.jar "$embeddedMavenProfileJar" > /tmp/$timestamp/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
	echo Failed local blade.jar diff with downloaded maven profile jar. The embedded maven profile jar and nexus maven profile jar are not identical
	exit 1
fi

# Now lets go ahead and publish the blade cli jar for real since the embedded maven profile was correct

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} --refresh-dependencies :cli:publish --info ${scanOpt} > /tmp/$timestamp/blade-cli-publish-command.txt; retcode=$?
bladeCliPublishCommand=$(cat /tmp/$timestamp/blade-cli-publish-command.txt)

if [ "$retcode" != "0" ] || [ -z "$bladeCliPublishCommand" ]; then
	echo Failed :cli:publish
	exit 1
fi

# Grep the output of the blade jar publish to find the url
bladeCliJarUrl=$(echo "$bladeCliPublishCommand" | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f4)

# download the just published jar in order to extract the embedded maven profile jar to compare to previously downloaded version from above (just to be double sure)
bladeCliUrl="${repoHost}${bladeCliJarUrl}"

curl -s "$bladeCliUrl" -o /tmp/$timestamp/blade.jar

if [ "$?" != "0" ]; then
	echo Downloading blade jar failed.
	exit 1
else
	echo "Published $bladeCliUrl"
fi

unzip -p /tmp/$timestamp/blade.jar "$embeddedMavenProfileJar" > /tmp/$timestamp/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
	echo Failed local blade.jar diff with downloaded maven profile jar. The embedded maven profile jar and nexus maven profile jar are not identical
	exit 1
fi

localBladeVersion=$(java -jar /tmp/$timestamp/blade.jar version)

# Already handled in the dockerfile
# mkdir ~/.blade

echo "$repoHost/nexus/content/groups/public/com/liferay/blade/com.liferay.blade.cli/" > ~/.blade/update.url

if [ "$releaseType" = "snapshots" ]; then
	bladeUpdate=$(blade update --snapshots)
else
	bladeUpdate=$(blade update)
fi

if [ "$?" != "0" ]; then
	echo Failed blade update.
	echo $bladeUpdate
	exit 1
fi

updatedBladeVersion=$(blade version)

echo $bladeUpdate
echo $localBladeVersion
echo $updatedBladeVersion

if [ "$localBladeVersion" != "$updatedBladeVersion" ]; then
	echo "After blade updated versions do not match."
	echo "Built blade version = $localBladeVersion"
	echo "Updated blade version = $updatedBladeVersion"
	exit 1
fi