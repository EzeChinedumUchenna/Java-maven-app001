FROM adoptopenjdk/openjdk8:x86_64-alpine-jre8u232-b09
EXPOSE 8080
COPY ./targetss/java-maven-app-* /usr/app/
WORKDIR /usr/app

CMD java -jar java-maven-app-*.jar
