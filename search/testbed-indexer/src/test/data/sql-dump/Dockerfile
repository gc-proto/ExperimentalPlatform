FROM ubuntu:bionic

ENV DB_REMOTE_ROOT_PASS="PASSWORD" \
    DB_REMOTE_ROOT_NAME="Norconex" \
    DB_REMOTE_ROOT_HOST="%" \ 
    DB_NAME="awr_schema" \
    DUMPFILE="Dump20191029_awr_schema.sql.zip" \
    MYSQL_USER=mysql \
    MYSQL_USERID=8080 \
    MYSQL_GROUP=mysql \
    MYSQL_GROUPID=8080 \
    MYSQL_VERSION=5.7.27 \
    MYSQL_DATA_DIR=/var/lib/mysql \
    MYSQL_RUN_DIR=/run/mysqld \
    MYSQL_LOG_DIR=/var/log/mysql

# Add the indexer user to run the java indexer in the entrypoint
RUN set -x; \
  groupadd -r --gid "$MYSQL_GROUPID" "$MYSQL_GROUP"; \
  useradd -r --uid "$MYSQL_USERID" --gid "$MYSQL_GROUPID" "$MYSQL_USER"
 
RUN apt-get update; \
    apt-get install unzip -y; \
    apt-get install mysql-server -y; \
    usermod -aG root ${MYSQL_USER}; \
    rm -rf ${MYSQL_DATA_DIR}; \
    rm -rf /var/lib/apt/lists/*

# Copy local project sql dump 
ADD --chown=mysql:mysql Dump20191029_awr_schema.sql.zip /opt/
COPY --chown=mysql:mysql entrypoint.sh /sbin/entrypoint.sh

RUN chmod 755  /sbin/entrypoint.sh; \
    unzip /opt/Dump20191029_awr_schema.sql.zip; \
    mkdir -p $MYSQL_DATA_DIR; \
    chown -R mysql:mysql "$MYSQL_DATA_DIR"; 

USER root
EXPOSE 3306

ENTRYPOINT ["/sbin/entrypoint.sh"]

CMD ["/usr/bin/mysqld_safe"]
