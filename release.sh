rm -rf mavenRepo
./gradlew clean
./gradlew -Prelease :extensions:maven-profile:publish
./gradlew -Prelease --refresh-dependencies :cli:publish
