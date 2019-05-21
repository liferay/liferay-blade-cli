#!/bin/bash
docker stop /local-nexus
docker rm /local-nexus

docker pull sonatype/nexus:2.14.11-01 && \
docker run -d -p 8081:8081 --name local-nexus sonatype/nexus:2.14.11-01

until $(curl --output /dev/null --silent --head --fail http://localhost:8081/nexus/); do
  printf '.'
  sleep 5
done