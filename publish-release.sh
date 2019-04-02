./gradlew --no-daemon clean && \
./gradlew --no-daemon -Prelease :extensions:maven-profile:publish --info --scan && \
./gradlew --no-daemon -Prelease :extensions:remote-deploy-command:publish --info --scan && \
./gradlew --no-daemon -Prelease --refresh-dependencies clean :cli:publish --info --scan
