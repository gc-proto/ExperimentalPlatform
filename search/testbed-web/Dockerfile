FROM java:8
EXPOSE 8080
ADD /target/testbed-web-0.0.1-SNAPSHOT-exec.jar testbed-web-0.0.1-SNAPSHOT-exec.jar
ENTRYPOINT ["java","-jar", "-Dsolr.url=http://testbed-solr:8983/solr", "testbed-web-0.0.1-SNAPSHOT-exec.jar"]