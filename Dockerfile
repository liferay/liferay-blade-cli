FROM openjdk:8-stretch

ENV JAVA_TOOL_OPTIONS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Xmx4096m
ENV GRADLE_OPTS -Xmx4G

COPY . /src

WORKDIR /src

ENTRYPOINT ["./docker-entrypoint.sh"]

CMD ["clean", "build", "--scan"]