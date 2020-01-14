ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE as src

FROM nginx:alpine

COPY --from=src /var/www/html /var/www/html
COPY ./docker/conf/nginx.conf /etc/nginx/nginx.conf
RUN yes | rm -R /var/www/html
COPY docker/images/tbs-proto1.openplus.ca/ /var/www/html

