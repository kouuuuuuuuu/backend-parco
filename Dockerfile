#FROM openjdk:17-jdk-alpine
#COPY target/Eparking-0.0.1-SNAPSHOT.jar Eparking-0.0.1-SNAPSHOT.jar
#EXPOSE 8000
#CMD ["java", "-jar", "Eparking-0.0.1-SNAPSHOT.jar"]

FROM openjdk:11
COPY target/springboot-capstone-project.jar springboot-capstone-project.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/springboot-capstone-project.jar"]

