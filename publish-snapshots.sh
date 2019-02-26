./gradlew --no-daemon clean && \
./gradlew --no-daemon -Psnapshots :extensions:maven-profile:publish --info --scan && \
./gradlew --no-daemon -Psnapshots --refresh-dependencies :cli:publish --info --scan