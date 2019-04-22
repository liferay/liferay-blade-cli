# check the arguments first
if [ "${1}" != "release" -a "${1}" != "snapshots" ]; then
	echo "Must have one argument, either \"release\" or \"snapshots\"."
	exit 1
fi
  
# Setup a temp directory
timestamp=$(date +%s)
tmpDir="/tmp/$timestamp/"

mkdir -p $tmpDir

if [ -z "$repoHost" ]; then
  repoHost="https://repository.liferay.com"
fi

localNexusOpt=""

if [ "$repoHost" == "http://localhost:8081" ]; then
    localNexusOpt="-PlocalNexus"

    docker stop /local-nexus
    docker rm /local-nexus

    docker pull sonatype/nexus:2.14.11-01 && \
    docker run -d -p 8081:8081 --name local-nexus sonatype/nexus:2.14.11-01

    until $(curl --output /dev/null --silent --head --fail http://localhost:8081/nexus/); do
      printf '.'
      sleep 5
    done
fi

# First clean local build folder to try to minimize variants
./gradlew --no-daemon --console=plain clean

if [ "$?" != "0" ]; then
   echo Failed clean.
   rm -rf /tmp/$timestamp
   exit 1
fi

# Publish the Remote Deploy Command jar to snapshots
remoteDeployCommandPublishCommand=$(./gradlew --no-daemon --console=plain $localNexusOpt -P${1} :extensions:remote-deploy-command:publish --info --scan)

echo "$remoteDeployCommandPublishCommand"

if [ -z "$remoteDeployCommandPublishCommand" ]; then
   echo Failed :extensions:remote-deploy-command:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

# Publish the Maven Profile jar to snapshots
mavenProfilePublishCommand=$(./gradlew --no-daemon --console=plain $localNexusOpt -P${1} :extensions:maven-profile:publish --info --scan)

echo "$mavenProfilePublishCommand"

if [ -z "$mavenProfilePublishCommand" ]; then
   echo Failed :extensions:maven-profile:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

# Grep the output of the previous command to find the url of the published jar
mavenProfilePublishUrl=$(echo "$mavenProfilePublishCommand" | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f2)

if [ -z "$mavenProfilePublishUrl" ]; then
   echo Failed grepping for mavenProfilePublishUrl
   rm -rf /tmp/$timestamp
   exit 1
fi

# Download the just published jar in order to later compare it to the embedded maven profile that is in blade jar
mavenProfileJarUrl="$repoHost/nexus/content/groups/public/$mavenProfilePublishUrl"

echo "$mavenProfileJarUrl"

curl -s "$mavenProfileJarUrl" -o /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Downloading maven.profile jar from snapshots failed.
   rm -rf /tmp/$timestamp
   exit 1
fi

# Build the blade cli jar locally, but don't publish.
bladeCliJarCommand=$(./gradlew --no-daemon --console=plain $localNexusOpt -P${1} --refresh-dependencies :cli:jar --info --scan)

echo "$bladeCliJarCommand"

if [ -z "$bladeCliJarCommand" ]; then
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
bladeCliPublishCommand=$(./gradlew --no-daemon --console=plain $localNexusOpt -P${1} --refresh-dependencies :cli:publish --info --scan)

echo "$bladeCliPublishCommand"

if [ -z "$bladeCliPublishCommand" ]; then
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
   echo Downloading blade jar from snapshots failed.
   rm -rf /tmp/$timestamp
   exit 1
fi

unzip -p /tmp/$timestamp/blade.jar "$embeddedMavenProfileJar" > /tmp/$timestamp/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Failed local blade.jar diff with downloaded maven profile jar.  The embedded maven profile jar and nexus maven profile jar are not identical
   rm -rf /tmp/$timestamp
   exit 1
fi

rm -rf /tmp/$timestamp