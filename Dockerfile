FROM amazoncorretto:21
RUN yum update -y && yum install unzip -y
EXPOSE 8080
COPY --chmod=0755 entrypoint.sh /home/
ENTRYPOINT ["sh", "/home/entrypoint.sh"]
