FROM maven:3.5.3-jdk-8 AS initial-maven-base

# Build the fluent-web module ahead and individually to cache the layer
FROM tinkericlee/maven-cache-fluent:1.0.0 AS fluent-web-build
LABEL tech.tinkmaster.title="fluent-web-build"
LABEL tech.tinkmaster.description="Fluent-web build stage of ToolBox multi-stage docker build"
WORKDIR /usr/src/fluent-maven
COPY pom.xml /usr/src/fluent-maven/pom.xml
# Failed to consecutive COPY file, similar issue link: https://github.com/moby/moby/issues/37965
RUN true
COPY fluent-web /usr/src/fluent-maven/fluent-web
RUN mvn --file fluent-web -Dmaven.repo.local=/usr/src/fluent-maven/.m2/repository install

# Build fluent except fluent-web
FROM fluent-web-build AS fluent-exclude-web-build
LABEL tech.tinkmaster.title="fluent-exclude-web-build"
LABEL tech.tinkmaster.description="Fluent except fluent-web build stage of ToolBox multi-stage docker build"
WORKDIR /usr/src/fluent-maven
COPY . /usr/src/fluent-maven/
RUN mvn -DskipWeb -Dmaven.repo.local=/usr/src/fluent-maven/.m2/repository install

# Create image for fluent
FROM openjdk:11.0.9.1-jre-buster
LABEL tech.tinkmaster.title="fluent-release-stage-docker-alpine"
LABEL tech.tinkmaster.description="Docker-release stage of fluent multi-stage docker build"
USER root
RUN set -ex; \
  groupadd --system --gid=1024 tinkmaster; \
  useradd --system --uid=1024 --gid=tinkmaster tinkmaster; \
  mkdir -p /usr/local/lib/tinkmaster/fluent /etc/tinkmaster/fluent /var/lib/tinkmaster/fluent; \
  chown -R tinkmaster:tinkmaster /usr/local/lib/tinkmaster /etc/tinkmaster/fluent /var/lib/tinkmaster/fluent
WORKDIR /var/lib/tinkmaster/fluent
RUN apt-get install bash awk sed grep coreutils
COPY --from=fluent-exclude-web-build /usr/src/fluent-maven/fluent-dist/target/tinkmaster-fluent-dist/tinkmaster-fluent-*.jar /usr/local/lib/tinkmaster/fluent/tinkmaster-fluent.jar
USER 1024:1024
ENV spring.config.additional-location=file:/etc/tinkmaster/fluent/
CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:MaxRAMFraction=2", "-jar","/usr/local/lib/tinkmaster/fluent/tinkmaster-fluent.jar"]