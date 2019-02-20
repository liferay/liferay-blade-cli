./gradlew clean && \
./gradlew -Psnapshots :extensions:maven-profile:publish --info --scan && \
./gradlew -Psnapshots --refresh-dependencies :cli:publish --info --scan