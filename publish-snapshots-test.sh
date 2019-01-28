docker stop /publish-snapshots-test
docker rm /publish-snapshots-test

docker pull sonatype/nexus:2.14.11-01 && \
docker run -d -p 8081:8081 --name publish-snapshots-test sonatype/nexus:2.14.11-01

until $(curl --output /dev/null --silent --head --fail http://localhost:8081/nexus/); do
  printf '.'
  sleep 5
done

./gradlew clean && \
./gradlew -PlocalNexus -Psnapshots :extensions:maven-profile:publish && \
./gradlew -PlocalNexus -Psnapshots --refresh-dependencies :cli:check :cli:smokeTests && \
./gradlew -PlocalNexus -Psnapshots :cli:publish