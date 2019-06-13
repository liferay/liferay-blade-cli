rm -rf ~/.m2/repository/com/liferay/blade
./gradlew --no-daemon clean
./gradlew --no-daemon :extensions:maven-profile:publishToMavenLocal --scan
./gradlew --no-daemon -PmavenLocal --continue clean check smokeTests --scan