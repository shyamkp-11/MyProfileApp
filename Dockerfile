FROM amazoncorretto:21

COPY target/MyProfileApp.jar MyProfileApp.jar

EXPOSE 8081
COPY --chmod=0755 entrypoint.sh /home/
ENTRYPOINT ["sh", "/home/entrypoint.sh"]