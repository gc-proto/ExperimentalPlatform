ARG BASE_IMAGE=drupalwxt/site-wxt:latest
FROM $BASE_IMAGE

COPY docker/bin /var/www/docker/bin
COPY docker/conf/drupal/default.settings.php /var/www/docker/conf/drupal/default.settings.php
COPY docker/conf/php.ini /usr/local/etc/php/php.ini
COPY docker/conf/drupal/settings.php /var/www/html/sites/default/settings.php
COPY docker/conf/drupal/www.conf /usr/local/etc/php-fpm.d

RUN yes | rm -R /var/www/html
WORKDIR /var/www/html
RUN apk add rsync
RUN git clone https://github.com/openplus/tbs-proto1.git .
RUN cp -R /var/www/html/themes/custom /tmp/custom
RUN git reset --hard origin/master
RUN git pull
RUN git status
RUN chown -R -f www-data:www-data /var/www/html/
RUN chmod -R ug+rwx /var/www/html
RUN chown -R -f www-data:www-data /tmp/
RUN chmod -R ug+rwx /tmp
RUN apk update
RUN apk add nano
COPY docker/images/ci/empty_experimentation_db.sql /tmp
RUN chmod ug+rwx /tmp/empty_experimentation_db.sql
RUN chown -R -f www-data:www-data /tmp/empty_experimentation_db.sql
RUN curl https://drupalconsole.com/installer -L -o drupal.phar
RUN mv drupal.phar /usr/local/bin/drupal
RUN chmod +x /usr/local/bin/drupal






# Reset Cache
RUN php -r 'opcache_reset();'

