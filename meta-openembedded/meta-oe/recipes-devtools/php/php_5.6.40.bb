require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=b602636d46a61c0ac0432bbf5c078fe4"

SRC_URI += "file://php5-change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://pthread-check-threads-m4.patch \
            file://0001-Add-lpthread-to-link.patch \
            file://acinclude-xml2-config.patch \
            file://0001-acinclude-use-pkgconfig-for-libxml2-config.patch \
            file://0001-PHP-5.6-LibSSL-1.1-compatibility.patch \
            file://php5-0001-acinclude.m4-don-t-unset-cache-variables.patch \
            "

SRC_URI_append_class-target = " \
                                file://php5-pear-makefile.patch \
                                file://php5-phar-makefile.patch \
                                file://php5-0001-opcache-config.m4-enable-opcache.patch \
                                "

SRC_URI[md5sum] = "44633604d2fece1f53f508bc16751b74"
SRC_URI[sha256sum] = "ffd025d34623553ab2f7fd8fb21d0c9e6f9fa30dc565ca03a1d7b763023fba00"

DEPENDS += "libmcrypt"
EXTRA_OECONF += "--with-mcrypt=${STAGING_DIR_TARGET}${exec_prefix} \
                 " 
