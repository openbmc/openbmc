require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=b602636d46a61c0ac0432bbf5c078fe4"

SRC_URI += "file://php5-change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://pthread-check-threads-m4.patch \
            file://0001-Add-lpthread-to-link.patch \
            file://acinclude-xml2-config.patch \
            file://0001-acinclude-use-pkgconfig-for-libxml2-config.patch \
            file://0001-PHP-5.6-LibSSL-1.1-compatibility.patch \
            "

SRC_URI_append_class-target = " \
                                file://php5-pear-makefile.patch \
                                file://php5-phar-makefile.patch \
                                file://php5-0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "5b98aa066567eca8e5738b8ef4a3545c"
SRC_URI[sha256sum] = "d65b231bbdd63be4439ef5ced965cfd63e62983429dbd4dfcfb49981593ebc03"

DEPENDS += "libmcrypt"
EXTRA_OECONF += "--with-mcrypt=${STAGING_DIR_TARGET}${exec_prefix} \
                 " 
