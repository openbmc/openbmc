require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=fb07bfc51f6d5e0c30b65d9701233b2e"

SRC_URI += "file://0001-acinclude.m4-don-t-unset-cache-variables.patch \
            file://0048-Use-pkg-config-for-FreeType2-detection.patch \
            file://0049-ext-intl-Use-pkg-config-to-detect-icu.patch \
            file://0001-Use-pkg-config-for-libxml2-detection.patch \
           "
SRC_URI_append_class-target = " \
                                file://pear-makefile.patch \
                                file://phar-makefile.patch \
                                file://0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "c893ff828945c274d90e026528142439"
SRC_URI[sha256sum] = "946f50dacbd2f61e643bb737021cbe8b1816e780ee7ad3e0cd999a1892ab0add"

PACKAGECONFIG[mysql] = "--with-mysqli=mysqlnd \
                        --with-pdo-mysql=mysqlnd \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"
PACKAGECONFIG[valgrind] = "--with-valgrind=${STAGING_DIR_TARGET}/usr,--with-valgrind=no,valgrind"

FILES_${PN}-fpm += "${sysconfdir}/php-fpm.d/www.conf.default"
