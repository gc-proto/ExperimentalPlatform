#ARG BASE_IMAGE=drupalwxt/site-wxt:latest
#FROM $BASE_IMAGE as src
FROM nginx:alpine
COPY ./docker/images/covid19inv_nginx/nginx.conf /etc/nginx/nginx.conf
RUN mkdir -p /var/www/html
COPY ./docker/images/covid19inv_nginx/covid19_en.html /var/www/html/
COPY ./docker/images/covid19inv_nginx/covid19_fr.html /var/www/html/




