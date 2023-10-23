#Docker file

#jdk 19 Image Start
FROM openjdk:19-jdk
#인자 정리 - Jar
ARG JAR_FILE=build/libs/*.jar
#Jar File Copy
COPY ${JAR_FILE} ice_users.jar

ENTRYPOINT ["java", "-jar", "/ice_users.jar"]
#ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/ice_users.jar"]

