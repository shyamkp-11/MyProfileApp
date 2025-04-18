FROM amazoncorretto:21
COPY target/MyProfileApp.jar /home/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/MyProfileApp.jar"]