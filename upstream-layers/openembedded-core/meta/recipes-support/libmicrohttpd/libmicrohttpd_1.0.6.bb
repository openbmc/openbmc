SUMMARY = "A small C library that is supposed to make it easy to run an HTTP server as part of another application"
HOMEPAGE = "http://www.gnu.org/software/libmicrohttpd/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=57d09285feac8a64efa878e692b14f36"
SECTION = "net"

DEPENDS = "file"

SRC_URI = "${GNU_MIRROR}/libmicrohttpd/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "bb5cfcadfc52dbd5eb512d6e2995e0361351c33e97a87aba426d3a4a7ba6cf70"

inherit autotools lib_package pkgconfig gettext

CFLAGS += "-pthread -D_REENTRANT"

EXTRA_OECONF += "--disable-static --with-gnutls=${STAGING_LIBDIR}/../ --enable-largefile"

PACKAGECONFIG ?= "curl https"

PACKAGECONFIG[curl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[https] = "--enable-https,--disable-https,libgcrypt gnutls,"

do_compile:append() {
    sed -i s:-L${STAGING_LIBDIR}::g libmicrohttpd.pc
}

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2025-59777] = "fixed-version: fixed since 1.0.3"
CVE_STATUS[CVE-2025-62689] = "fixed-version: fixed since 1.0.3"
