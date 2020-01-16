#ARG BASE_IMAGE=drupalwxt/site-wxt:latest
#FROM $BASE_IMAGE as src
FROM nginx:alpine
COPY ./docker/conf/nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /var/www/html
RUN apk update
RUN apk add git
WORKDIR /var/www/html
RUN git clone https://github.com/openplus/tbs-proto1.git .



