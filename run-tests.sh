rm -rf ~/.m2/repositories/com/liferay/blade/com.liferay.blade.extensions.maven.profile
./gradlew clean
./gradlew :extensions:maven-profile:publishToMavenLocal
./gradlew -PmavenLocal -continue check smokeTests --scan