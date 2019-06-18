rm -rf ~/.m2/repository/com/liferay/blade
./gradlew --no-daemon clean
./gradlew --no-daemon :extensions:maven-profile:publishToMavenLocal -x :cli:bladeExtensionsVersions -x :cli:processResources --scan
./gradlew --no-daemon -PmavenLocal --continue clean check smokeTests --scan