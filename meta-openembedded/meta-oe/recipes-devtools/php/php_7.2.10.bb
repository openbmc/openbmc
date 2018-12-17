require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=67e369bc8d1f2e641236b8002039a6a2"

SRC_URI += "file://change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://0001-acinclude.m4-skip-binconfig-check-for-libxml.patch \
            file://0001-fix-error-caused-by-a-new-variable-is-declared-after.patch \
            file://CVE-2017-9120.patch \
           "
SRC_URI_append_class-target = " \
                                file://pear-makefile.patch \
                                file://phar-makefile.patch \
                                file://0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "0ce8ff615bfb9de7a89bab8d742c11c0"
SRC_URI[sha256sum] = "01b6129a0921a1636b07da9bc598a876669e45a462cef4b5844fc26862dbda9d"

PACKAGECONFIG[mysql] = "--with-mysqli=${STAGING_BINDIR_CROSS}/mysql_config \
                        --with-pdo-mysql=${STAGING_BINDIR_CROSS}/mysql_config \
                        ,--without-mysqli --without-pdo-mysql \
                        ,mysql5"
PACKAGECONFIG[valgrind] = "--with-valgrind=${STAGING_DIR_TARGET}/usr,--with-valgrind=no,valgrind"

FILES_${PN}-fpm += "${sysconfdir}/php-fpm.d/www.conf.default"
