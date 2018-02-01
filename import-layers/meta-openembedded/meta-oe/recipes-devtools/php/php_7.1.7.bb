require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=c0af599f66d0461c5837c695fcbc5c1e"

SRC_URI += "file://change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://0001-Specify-tag-with-libtool.patch \
           "
SRC_URI[md5sum] = "22e11a372f99afbbbf3f46a31e8a82ca"
SRC_URI[sha256sum] = "079b6792987f38dc485f92258c04f9e02dedd593f9d260ebe725343f812d1ff8"

PACKAGECONFIG[mysql] = "--with-mysqli=${STAGING_BINDIR_CROSS}/mysql_config \
                        --with-pdo-mysql=${STAGING_BINDIR_CROSS}/mysql_config \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"

FILES_${PN}-fpm += "${sysconfdir}/php-fpm.d/www.conf.default"
