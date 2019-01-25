rm -rf mavenRepo
./gradlew clean
./gradlew -Psnapshots :extensions:maven-profile:publish
./gradlew -Psnapshots --refresh-dependencies :cli:publish