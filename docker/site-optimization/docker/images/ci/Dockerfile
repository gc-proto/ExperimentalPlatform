ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE


RUN yes | rm -R /var/www/html
COPY docker/images/tbs-proto1.openplus.ca/ /var/www/html
COPY docker/bin /var/www/docker/bin
COPY docker/conf/drupal/default.settings.php /var/www/docker/conf/drupal/default.settings.php
COPY docker/conf/php.ini /usr/local/etc/php/php.ini
COPY docker/conf/drupal/settings.php /var/www/html/sites/default/settings.php



## START CI

WORKDIR /var/www
RUN composer install --prefer-dist --no-interaction

RUN ln -s /var/www/vendor/bin/behat /usr/local/bin/behat; \
    ln -s /var/www/vendor/bin/phpcs /usr/local/bin/phpcs; \
    ln -s /var/www/vendor/bin/phpmd /usr/local/bin/phpmd; \
    ln -s /var/www/vendor/bin/phpunit /usr/local/bin/phpunit; \
    ln -s /var/www/vendor/bin/drupal /usr/local/bin/drupal

RUN phpcs --config-set installed_paths /var/www/vendor/drupal/coder/coder_sniffer

## END CI

WORKDIR /var/www/html

# Reset Cache
RUN php -r 'opcache_reset();'
