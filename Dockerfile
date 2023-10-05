FROM adoptopenjdk/openjdk8:x86_64-alpine-jre8u232-b09
EXPOSE 8080
COPY ./target/java-maven-app-* /usr/app/       
// Note:  we used ./target/java-maven-app-* instead of ./target/java-maven-app-1.0.0-SNAPSHOT so that when there is version increment during build docker Build can be able to run Dockerfile 
// without any issue. So instead of hardcoding it, we will use * to accomodate all
WORKDIR /usr/app

CMD java -jar java-maven-app-*.jar
