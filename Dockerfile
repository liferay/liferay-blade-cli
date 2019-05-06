FROM openjdk:8-stretch

ENV JAVA_TOOL_OPTIONS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Xmx4096m
ENV GRADLE_OPTS -Xmx4G

CMD ["mkdir", "-p", "/root/.gradle/"]

COPY . /src

WORKDIR /src

ENTRYPOINT ["./publish.sh"]

CMD ["snapshots"]