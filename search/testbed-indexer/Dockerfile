FROM ubuntu:bionic

# Add SOLR_URL can be pass using -e in docker run to be specific
# Example, run using command below
# "docker run --name test-indexer -e SOLR_URL='http://123abc.test/solr' imageName" 
ENV PATH="/opt/indexer-data:/opt/docker-scripts:$PATH" \
    SOLR_URL="http://testbed_solr:8983/solr" \
    ZNAME="testbed-indexer-0.0.1-SNAPSHOT" \
    ZFORMAT="tar" \
    SPATH="/opt/docker-scripts" \
    INDEXER_USER="indexer" \
    INDEXER_UID="8080" \
    INDEXER_GROUP="indexer" \
    INDEXER_GID="8080" \
    INDEXER_HOME=/opt/indexer-data \
    INDEXER_PID_DIR=/home/indexer 



# Add the indexer user to run the java indexer in the entrypoint
RUN set -x; \
  groupadd -r --gid "$INDEXER_GID" "$INDEXER_GROUP"; \
  useradd -r --uid "$INDEXER_UID" --gid "$INDEXER_GID" "$INDEXER_USER"
 
RUN mkdir -p /opt/indexer-data; \
    chown -R indexer:indexer "/opt/indexer-data"; \
    mkdir -p /opt/docker-scripts; \
    chown -R indexer:indexer "/opt/docker-scripts"; \
    mkdir -p /home/indexer; \
    chown -R indexer:indexer "/home/indexer"; \
    usermod -aG root indexer; \
    apt-get update; \
    apt-get install -y openjdk-8-jre-headless;

USER root
COPY ./certs /tmp/certs
RUN yes | keytool -importcert -alias www.tc.gc.ca -keystore /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -storepass changeit -file /tmp/certs/www.tc.gc.ca.cer
RUN rm -R /tmp/certs

# Add below to debug
#    apt-get install nginx -y

# Making sure that the user indexer can access these files
# COPY and extract at the same time the tar file.
ADD --chown=indexer:indexer /target/$ZNAME.$ZFORMAT /opt/docker-scripts/
COPY --chown=indexer:indexer src/test/data/ /opt/indexer-data
COPY --chown=indexer:indexer docker-scripts/ /opt/docker-scripts



# Validate files transfer in docker build and unix format the sh file
RUN chmod +x /opt/docker-scripts/*; \
    ls -all; \
    ls /opt -Rtall; \
    echo $PATH; \
    chown -R indexer:indexer "/opt/docker-scripts"; \
    sed -i -e 's/\r$//' /opt/docker-scripts/docker-entrypoint; \
    sed -i -e 's/\r$//' /opt/docker-scripts/indexer-run; \
    cat /opt/docker-scripts/docker-entrypoint

COPY --chown=indexer:indexer configs/ /opt/indexer-configs/

USER $INDEXER_USER



# Entry point when running docker container (comment below to debug)
ENTRYPOINT ["docker-entrypoint"]

# The Java class run script happens in here (comment below to debug)
CMD ["indexer-run"]

# Add below to debug, must run as `--user=root` in docker run
# CMD ["nginx","-g","daemon off;"]