ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE

COPY docker/bin /var/www/docker/bin
COPY docker/conf/drupal/default.settings.php /var/www/docker/conf/drupal/default.settings.php
COPY docker/conf/php.ini /usr/local/etc/php/php.ini
COPY docker/conf/drupal/settings.php /var/www/html/sites/default/settings.php
COPY docker/conf/drupal/www.conf /usr/local/etc/php-fpm.d


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

RUN yes | rm -R /var/www/html
RUN git clone https://github.com/openplus/tbs-proto1.git .

COPY docker/images/tbs-proto1.openplus.ca/modules/custom/openplus_migrate /var/www/html/modules/custom/openplus_migrate
COPY docker/images/tbs-proto1.openplus.ca/modules/contrib/schema_metatag /var/www/html/modules/contrib/schema_metatag


#RUN composer require drupal/s3fs
RUN cp -R /var/www/html/themes/custom /tmp/custom

RUN apk update
RUN apk add rsync

# Reset Cache
RUN php -r 'opcache_reset();'

