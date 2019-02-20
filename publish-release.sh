./gradlew clean && \
./gradlew -Prelease :extensions:maven-profile:publish --info --scan && \
./gradlew -Prelease --refresh-dependencies :cli:publish --info --scan