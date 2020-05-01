ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE
COPY docker/bin /var/www/docker/bin
COPY docker/images/pageperformance_php/php.ini /usr/local/etc/php/php.ini
RUN yes | rm -R /var/www/html
WORKDIR /var/www/html
RUN git clone https://github.com/amonsour/analytics-api.git .
COPY docker/images/pageperformance_php/secrets/php/config.php /var/www/html/php/config.php
COPY docker/images/pageperformance_php/secrets/keys /var/www/html/keys
RUN chown -R -f www-data:www-data /var/www/html/
RUN chmod -R ug+rwx /var/www/html
RUN apk update
RUN apk add nano
RUN apk add curl
RUN apk add php-curl
RUN apk add openssl
RUN apk add php-openssl
RUN apk add --no-cache $PHPIZE_DEPS
RUN pecl install mongodb
COPY docker/images/pageperformance_php/mongo_connect.php /var/www/html/php/
# Reset Cache
RUN php -r 'opcache_reset();'

