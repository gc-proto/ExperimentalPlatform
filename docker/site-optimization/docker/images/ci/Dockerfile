ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE

COPY docker/bin /var/www/docker/bin
COPY docker/conf/drupal/default.settings.php /var/www/docker/conf/drupal/default.settings.php
COPY docker/conf/php.ini /usr/local/etc/php/php.ini
COPY docker/conf/drupal/settings.php /var/www/html/sites/default/settings.php
COPY docker/conf/drupal/www.conf /usr/local/etc/php-fpm.d

RUN yes | rm -R /var/www/html
WORKDIR /var/www/html
RUN git clone https://github.com/openplus/tbs-proto1.git .
RUN cp -R /var/www/html/themes/custom /tmp/custom
RUN git reset --hard origin/master
RUN git pull
RUN git status
RUN chown -R -f www-data:www-data /var/www/html/
RUN chmod -R ug+rwx /var/www/html
RUN apk update
RUN apk add rsync
RUN apk add nano



# Reset Cache
RUN php -r 'opcache_reset();'

