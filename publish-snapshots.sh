./gradlew clean && \
./gradlew -Psnapshots :extensions:maven-profile:publish --info && \
./gradlew -Psnapshots --refresh-dependencies :cli:publish --info