ARG BASE_IMAGE=openplusca/site-canada:latest
FROM $BASE_IMAGE

COPY docker/bin /var/www/docker/bin
COPY docker/conf/drupal/default.settings.php /var/www/docker/conf/drupal/default.settings.php
COPY docker/conf/php.ini /usr/local/etc/php/php.ini
COPY docker/conf/drupal/settings.php /var/www/html/sites/default/settings.php
COPY docker/conf/drupal/www.conf /usr/local/etc/php-fpm.d
# Reset Cache
RUN php -r 'opcache_reset();'



