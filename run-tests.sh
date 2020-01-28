rm -rf ~/.m2/repository/com/liferay/blade
./gradlew --no-daemon clean
./gradlew --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan
./gradlew --no-daemon -PmavenLocal --continue clean check smokeTests --scan
find . -name index.html | grep build/reports/tests | sed 's/\/test\/index\.html//' | xargs zip -o tests.zip -r