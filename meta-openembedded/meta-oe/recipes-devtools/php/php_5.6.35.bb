require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=b602636d46a61c0ac0432bbf5c078fe4"

SRC_URI += "file://php5-change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://pthread-check-threads-m4.patch \
            file://0001-Add-lpthread-to-link.patch \
            file://acinclude-xml2-config.patch \
            file://0001-acinclude-use-pkgconfig-for-libxml2-config.patch \
            "

SRC_URI_append_class-target = " \
                                file://php5-pear-makefile.patch \
                                file://php5-phar-makefile.patch \
                                file://php5-0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "905ae5f586351f3ca29d044c9484d475"
SRC_URI[sha256sum] = "ee78a7e9ca21d8ea394d037c55effff477a49dbae31c7753c547036f5bd73b92"

DEPENDS += "libmcrypt"
EXTRA_OECONF += "--with-mcrypt=${STAGING_DIR_TARGET}${exec_prefix} \
                 " 
