

snapshostUrl="https://repository-cdn.liferay.com"

timestamp=$(date +%s)
mkdir -p /tmp/$timestamp/cli

./gradlew --no-daemon clean
if [ "$?" != "0" ]; then
   echo Failed clean.
   rm -rf /tmp/$timestamp
   exit 1
fi

mavenProfile=`./gradlew --no-daemon -Psnapshots :extensions:maven-profile:publish --info --scan | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f2`
if [ "$?" != "0" ]; then
   echo Failed :extensions:maven-profile:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

mavenProfile="$snapshostUrl/nexus/content/groups/public/"$mavenProfile

curl -s $mavenProfile -o /tmp/$timestamp/maven_profile.jar

./gradlew --no-daemon -Psnapshots :extensions:remote-deploy-command:publish --info --scan
if [ "$?" != "0" ]; then
   echo Failed :extensions:remote-deploy-command:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

bladeCli=`./gradlew --no-daemon -Psnapshots --refresh-dependencies :cli:publish --info --scan | grep Uploading | grep '.jar ' | grep -v -e '-sources' -e '-tests' | cut -d' ' -f2`
if [ "$?" != "0" ]; then
   echo Failed :extensions:cli:publish
   rm -rf /tmp/$timestamp
   exit 1
fi

bladeCli="$snapshostUrl/nexus/content/groups/public/"$bladeCli

curl -s $bladeCli -o /tmp/$timestamp/cli/blade.jar

mavenProfileJar=`jar -tf /tmp/$timestamp/cli/blade.jar | grep "maven.profile-"`

unzip -p /tmp/$timestamp/cli/blade.jar $mavenProfileJar > /tmp/$timestamp/cli/myExtractedMavenProfile.jar

diff -s /tmp/$timestamp/cli/myExtractedMavenProfile.jar /tmp/$timestamp/maven_profile.jar

if [ "$?" != "0" ]; then
   echo Failed diff
   rm -rf /tmp/$timestamp
   exit 1
fi

rm -rf /tmp/$timestamp

