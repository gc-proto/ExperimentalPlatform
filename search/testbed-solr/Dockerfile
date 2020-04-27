FROM ubuntu:eoan

ENV SOLR_USERID=8983 \
    SOLR_USER=solr \
    SOLR_GROUP=solr \
    SOLR_GROUPID=8983 \
    SOLR_SERVER_VERSION=8.5.0 \
    SOLR_ULIMIT_CHECKS=65000 \
    SOLR_ROOT_PATH=/var/solr \
    SOLR_HOME=/var/solr/server/solr \
    SOLR_DATA_DIR=/var/solr/server/solr \
    SOLR_RUN_DIR=/var/solr/bin \
    SOLR_LOG_DIR=/var/solr/server/logs \
    PATH="/var/solr/bin:/var/docker-scripts:$PATH"

# Add the indexer user to run the java indexer in the entrypoint
RUN set -x; \
  groupadd -r --gid "$SOLR_GROUPID" "$SOLR_GROUP"; \
  useradd -r --uid "$SOLR_USERID" --gid "$SOLR_GROUPID" "$SOLR_USER"; \
  mkdir -p /var/docker-scripts; \
  chown -R solr:solr "/var/docker-scripts";

COPY --chown=solr:solr docker-scripts/ /var/docker-scripts

# Install all the tools needed in this linux to start others
RUN apt-get update; \
    apt-get install -y openjdk-11-jre-headless; \
    apt-get install -y lsof; \
    apt-get install wget -y; \ 
    apt-get install unzip;

# Add below to debug
#RUN apt-get install nginx -y;

# Download Solr Server installation package
RUN set -x; \
    wget -nv https://archive.apache.org/dist/lucene/solr/"$SOLR_SERVER_VERSION"/solr-"$SOLR_SERVER_VERSION".zip;

# Extract and setup the folders for data
RUN set -x; \
    unzip solr-"$SOLR_SERVER_VERSION".zip -d /var;  \
    sed -i -e 's/\r$//' /var/docker-scripts/docker-entrypoint; \
    sed -i -e 's/\r$//' /var/docker-scripts/solr-run; \
    chmod 755 /var/docker-scripts/*; 

# Remove the downloaded file to save some space in the docker image
RUN rm solr-"$SOLR_SERVER_VERSION".zip; \ 
    mv /var/solr-"$SOLR_SERVER_VERSION" "$SOLR_ROOT_PATH"; \ 
    chown -R solr:solr "$SOLR_ROOT_PATH"; \
    sed -i -e 's/# SOLR_ULIMIT_CHECKS=false either here or as part of your profile./SOLR_ULIMIT_CHECKS=false/g' "$SOLR_ROOT_PATH"/bin/solr.in.sh;

# Copy files to the Solr Web-App for extraction
RUN cp -R "${SOLR_ROOT_PATH}/contrib/extraction/lib" "${SOLR_ROOT_PATH}/server/solr-webapp/webapp/WEB-INF"; \
    cp "${SOLR_ROOT_PATH}/dist/solr-cell-${SOLR_SERVER_VERSION}.jar" "${SOLR_ROOT_PATH}/server/solr-webapp/webapp/WEB-INF/lib/"; \
    chown -R solr:solr "$SOLR_ROOT_PATH/server/solr-webapp/webapp/WEB-INF"; 
 
# Add the Solr core into the existing solr data path
COPY --chown=solr:solr src/main/config/ "$SOLR_DATA_DIR"

USER "$SOLR_USER"
EXPOSE 8983
VOLUME "$SOLR_HOME"

# Entry point when running docker container
ENTRYPOINT ["docker-entrypoint"]

# The run script happens in here
CMD ["solr-run"]

# Add below to debug, must run as `--user=root` in docker run
# Getting int the docker using `docker exec -it mydockername bash`
# Comment out the above ENTRYPOINT and CMD then uncomment below to use debug
#CMD ["nginx","-g","daemon off;"]
