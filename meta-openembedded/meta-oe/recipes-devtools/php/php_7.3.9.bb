require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=fb07bfc51f6d5e0c30b65d9701233b2e"

SRC_URI += "file://0001-acinclude.m4-don-t-unset-cache-variables.patch \
            file://0048-Use-pkg-config-for-FreeType2-detection.patch \
            file://0049-ext-intl-Use-pkg-config-to-detect-icu.patch \
            file://0001-Use-pkg-config-for-libxml2-detection.patch \
            file://debian-php-fixheader.patch \
           "
SRC_URI_append_class-target = " \
                                file://pear-makefile.patch \
                                file://phar-makefile.patch \
                                file://0001-opcache-config.m4-enable-opcache.patch \
                                file://xfail_two_bug_tests.patch \
                                "

SRC_URI[md5sum] = "bcc37749815009c9201e4c126e1ab8ee"
SRC_URI[sha256sum] = "a39c9709a8c9eb7ea8ac4933ef7a78b92f7e5735a405c8b8e42ee39541d963c4"
