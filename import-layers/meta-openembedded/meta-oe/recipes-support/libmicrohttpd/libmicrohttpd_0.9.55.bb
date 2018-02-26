DESCRIPTION = "A small C library that is supposed to make it easy to run an HTTP server as part of another application"
HOMEPAGE = "http://www.gnu.org/software/libmicrohttpd/"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=9331186f4f80db7da0e724bdd6554ee5"
SECTION = "net"
DEPENDS = "libgcrypt gnutls file"

SRC_URI = "http://ftp.gnu.org/gnu/libmicrohttpd/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "1c20f84a8b9cf692dd50b558b3571a3a"
SRC_URI[sha256sum] = "0c1cab8dc9f2588bd3076a28f77a7f8de9560cbf2d80e53f9a8696ada80ed0f8"

inherit autotools lib_package pkgconfig gettext

EXTRA_OECONF += "--disable-static --with-gnutls=${STAGING_LIBDIR}/../"

PACKAGECONFIG ?= "curl"
PACKAGECONFIG_append_class-target = "\
        ${@bb.utils.filter('DISTRO_FEATURES', 'largefile', d)} \
"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,"

do_compile_append() {
    sed -i s:-L${STAGING_LIBDIR}::g libmicrohttpd.pc
}
