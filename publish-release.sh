./gradlew --no-daemon clean && \
./gradlew --no-daemon -Prelease --refresh-dependencies clean :cli:publish --info --scan