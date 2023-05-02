DESCRIPTION = "A small C library that is supposed to make it easy to run an HTTP server as part of another application"
HOMEPAGE = "http://www.gnu.org/software/libmicrohttpd/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=57d09285feac8a64efa878e692b14f36"
SECTION = "net"

DEPENDS = "file"

SRC_URI = "${GNU_MIRROR}/libmicrohttpd/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "f0b1547b5a42a6c0f724e8e1c1cb5ce9c4c35fb495e7d780b9930d35011ceb4c"

inherit autotools lib_package pkgconfig gettext

CFLAGS += "-pthread -D_REENTRANT"

EXTRA_OECONF += "--disable-static --with-gnutls=${STAGING_LIBDIR}/../"

PACKAGECONFIG ?= "curl https"
PACKAGECONFIG:append:class-target = "\
        ${@bb.utils.filter('DISTRO_FEATURES', 'largefile', d)} \
"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[https] = "--enable-https,--disable-https,libgcrypt gnutls,"

do_compile:append() {
    sed -i s:-L${STAGING_LIBDIR}::g libmicrohttpd.pc
}

BBCLASSEXTEND = "native nativesdk"
