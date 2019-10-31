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

bladeTestOpt="check"
nexusOpt=""
releaseType=""
scanOpt="--scan"

# check the arguments first
while [ $# -gt 0 ]; do
	if [ "$1" = "snapshots" ] || [ "$1" = "release" ]; then
		releaseType="$1"
	elif [ "$1" = "--local" ]; then
		nexusOpt="-PlocalNexus"
	elif [ "$1" = "--remote" ]; then
		nexusOpt="-PremoteNexus"
	elif [ "$1" = "--skip-tests" ]; then
		bladeTestOpt="jar"
	elif [ "$1" = "--skip-scan" ]; then
		scanOpt=""
	fi
	shift
done

if [ "$releaseType" != "release" ] && [ "$releaseType" != "snapshots" ]; then
	echo "Must have one argument, either \"release\" or \"snapshots\"."
	exit 1
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

# First clean local build folder to try to minimize variants

./gradlew -q --no-daemon --console=plain clean

if [ "$?" != "0" ]; then
   echo Failed clean.
   rm -rf /tmp/$timestamp
   exit 1
fi

# Publish the Remote Deploy Command jar

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:remote-deploy-command:publish --info ${scanOpt} | tee /tmp/$timestamp/remote-deploy-publish-command.txt
remoteDeployCommandPublishCommand=$(cat /tmp/$timestamp/remote-deploy-publish-command.txt)

if [ "$?" != "0" ] || [ -z "$remoteDeployCommandPublishCommand" ]; then
   echo Failed :extensions:remote-deploy-command:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

# Publish the Maven Profile jar
./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} :extensions:maven-profile:publish -x :cli:bladeExtensionsVersions -x :cli:processResources --info ${scanOpt} | tee /tmp/$timestamp/maven-profile-publish-command.txt
mavenProfilePublishCommand=$(cat /tmp/$timestamp/maven-profile-publish-command.txt)

if [ "$?" != "0" ] || [ -z "$mavenProfilePublishCommand" ]; then
   echo Failed :extensions:maven-profile:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

# Grep the output of the previous command to find the url of the published jar
mavenProfilePublishUrl=$(echo "$mavenProfilePublishCommand" | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f2)

if [ "$?" != "0" ] || [ -z "$mavenProfilePublishUrl" ]; then
   echo Failed grepping for mavenProfilePublishUrl
   #rm -rf /tmp/$timestamp
   exit 1
fi

# Download the just published jar in order to later compare it to the embedded maven profile that is in blade jar
mavenProfileJarUrl="$repoHost/nexus/content/groups/public/$mavenProfilePublishUrl"

curl -s "$mavenProfileJarUrl" -o /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Downloading maven.profile jar failed.
   rm -rf /tmp/$timestamp
   exit 1
else
   echo "Published $mavenProfileJarUrl"
fi

# Test the blade cli jar locally, but don't publish.

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} --refresh-dependencies clean $bladeTestOpt --info ${scanOpt} | tee /tmp/$timestamp/blade-cli-jar-command.txt

bladeCliJarCommand=$(cat /tmp/$timestamp/blade-cli-jar-command.txt)

if [ "$?" != "0" ] || [ -z "$bladeCliJarCommand" ]; then
   echo Failed :cli:jar
   rm -rf /tmp/$timestamp
   exit 1
fi

# now that we have the blade jar just built, lets extract the embedded maven profile jar and compare to the maven profile downloaded from nexus

embeddedMavenProfileJar=$(jar -tf cli/build/libs/blade.jar | grep "maven.profile-")

if [ -z "$embeddedMavenProfileJar" ]; then
   echo Failed to find embedded maven.profile jar in blade jar
   rm -rf /tmp/$timestamp
   exit 1
fi

unzip -p cli/build/libs/blade.jar "$embeddedMavenProfileJar" > /tmp/$timestamp/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Failed local blade.jar diff with downloaded maven profile jar.  The embedded maven profile jar and nexus maven profile jar are not identical
   rm -rf /tmp/$timestamp
   exit 1
fi

# Now lets go ahead and publish the blade cli jar for real since the embedded maven profile was correct

./gradlew -q --no-daemon --console=plain $nexusOpt -P${releaseType} --refresh-dependencies :cli:publish --info ${scanOpt} | tee /tmp/$timestamp/blade-cli-publish-command.txt
bladeCliPublishCommand=$(cat /tmp/$timestamp/blade-cli-publish-command.txt)

if [ "$?" != "0" ] || [ -z "$bladeCliPublishCommand" ]; then
   echo Failed :cli:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

# Grep the output of the blade jar publish to find the url
bladeCliJarUrl=$(echo "$bladeCliPublishCommand" | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f2)

# download the just published jar in order to extract the embedded maven profile jar to compare to previously downloaded version from above (just to be double sure)
bladeCliUrl="$repoHost/nexus/content/groups/public/$bladeCliJarUrl"

curl -s "$bladeCliUrl" -o /tmp/$timestamp/blade.jar

if [ "$?" != "0" ]; then
   echo Downloading blade jar failed.
   rm -rf /tmp/$timestamp
   exit 1
else
   echo "Published $bladeCliUrl"
fi

unzip -p /tmp/$timestamp/blade.jar "$embeddedMavenProfileJar" > /tmp/$timestamp/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Failed local blade.jar diff with downloaded maven profile jar.  The embedded maven profile jar and nexus maven profile jar are not identical
   rm -rf /tmp/$timestamp
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
   rm -rf /tmp/$timestamp
   exit 1
fi

updatedBladeVersion=$(blade version)

echo $bladeUpdate
echo $localBladeVersion
echo $updatedBladeVersion

if [ "$localBladeVersion" != "$updatedBladeVersion" ]; then
	echo After blade updated versions do not match.
	echo "Built blade version = $localBladeVersion"
	echo "Updated blade version = $updatedBladeVersion"
fi

rm -rf /tmp/$timestamp
