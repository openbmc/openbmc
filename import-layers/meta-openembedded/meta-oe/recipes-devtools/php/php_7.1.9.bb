require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=c0af599f66d0461c5837c695fcbc5c1e"

SRC_URI += "file://change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://0001-Specify-tag-with-libtool.patch \
            file://CVE-2017-16642.patch \
           "
SRC_URI[md5sum] = "2397be54f3281cdf30c7ef076b28f7d0"
SRC_URI[sha256sum] = "314dcc10dfdd7c4443edb4fe1e133a44f2b2a8351be8c9eb6ab9222d45fd9bae"

PACKAGECONFIG[mysql] = "--with-mysqli=${STAGING_BINDIR_CROSS}/mysql_config \
                        --with-pdo-mysql=${STAGING_BINDIR_CROSS}/mysql_config \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"

FILES_${PN}-fpm += "${sysconfdir}/php-fpm.d/www.conf.default"
