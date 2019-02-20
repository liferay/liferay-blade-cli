./gradlew clean && \
./gradlew -Prelease :extensions:maven-profile:publish --info && \
./gradlew -Prelease --refresh-dependencies :cli:publish --info