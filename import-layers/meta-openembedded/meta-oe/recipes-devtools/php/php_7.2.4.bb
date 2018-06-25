require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=67e369bc8d1f2e641236b8002039a6a2"

SRC_URI += "file://change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://0001-acinclude.m4-skip-binconfig-check-for-libxml.patch \
            file://0001-main-php_ini.c-build-empty-php_load_zend_extension_c.patch \
            file://0001-fix-error-caused-by-a-new-variable-is-declared-after.patch \
           "
SRC_URI_append_class-target = " \
                                file://pear-makefile.patch \
                                file://phar-makefile.patch \
                                file://0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "864c64ffd2f1686b035ef8ce6a6d8478"
SRC_URI[sha256sum] = "11658a0d764dc94023b9fb60d4b5eb75d438ad17981efe70abb0d0d09a447ef3"

PACKAGECONFIG[mysql] = "--with-mysqli=${STAGING_BINDIR_CROSS}/mysql_config \
                        --with-pdo-mysql=${STAGING_BINDIR_CROSS}/mysql_config \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"

FILES_${PN}-fpm += "${sysconfdir}/php-fpm.d/www.conf.default"
