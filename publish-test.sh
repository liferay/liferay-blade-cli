if [ "${1}" != "release" -a "${1}" != "snapshots" ]; then
	echo "Must have one argument, either \"release\" or \"snapshots\"."
	exit 1
fi

docker stop /local-nexus
docker rm /local-nexus

docker pull sonatype/nexus:2.14.11-01 && \
docker run -d -p 8081:8081 --name local-nexus sonatype/nexus:2.14.11-01

until $(curl --output /dev/null --silent --head --fail http://localhost:8081/nexus/); do
  printf '.'
  sleep 5
done

./gradlew clean && \
./gradlew -PlocalNexus -P${1} :extensions:maven-profile:publish --info && \
./gradlew -PlocalNexus -P${1} --refresh-dependencies clean check :cli:smokeTests && \
./gradlew -PlocalNexus -P${1} --refresh-dependencies :cli:publish --info