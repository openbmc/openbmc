DESCRIPTION = "A small C library that is supposed to make it easy to run an HTTP server as part of another application"
HOMEPAGE = "http://www.gnu.org/software/libmicrohttpd/"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=9331186f4f80db7da0e724bdd6554ee5"
SECTION = "net"
DEPENDS = "file"

SRC_URI = "http://ftp.gnu.org/gnu/libmicrohttpd/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "8ab5123535549195aff19e91b4e49f48"
SRC_URI[sha256sum] = "9b9ccd7d0b11b0e179f1f58dc2caa3e0c62c8609e1e1dc7dcaadf941b67d923c"

inherit autotools lib_package pkgconfig gettext

CFLAGS += "-pthread -D_REENTRANT"

EXTRA_OECONF += "--disable-static --with-gnutls=${STAGING_LIBDIR}/../"

PACKAGECONFIG ?= "curl https"
PACKAGECONFIG_append_class-target = "\
        ${@bb.utils.filter('DISTRO_FEATURES', 'largefile', d)} \
"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[https] = "--enable-https,--disable-https,libgcrypt gnutls,"

do_compile_append() {
    sed -i s:-L${STAGING_LIBDIR}::g libmicrohttpd.pc
}
