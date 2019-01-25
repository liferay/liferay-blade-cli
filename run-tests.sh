rm -rf mavenRepo
./gradlew clean
./gradlew -Psnapshots :extensions:maven-profile:publishToMavenLocal
./gradlew -Psnapshots -continue check smokeTests