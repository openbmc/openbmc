SUMMARY = "A portable version of the mg maintained by the OpenBSD team"
HOMEPAGE = "http://homepage.boetes.org/software/mg/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://version.c;md5=1895eb37bf6bd79cdc5c89d8166fabfb"
DEPENDS = "ncurses libbsd"
SECTION = "console/editors"

SRC_URI = "http://homepage.boetes.org/software/mg/mg-${PV}.tar.gz \
           file://0001-fileio-Include-sys-param.h-for-MAXNAMLEN.patch \
           file://0002-fileio-Define-DEFFILEMODE-if-platform-is-missing.patch \
           "
SRC_URI_append_libc-musl = "\
           file://0001-Undefine-REGEX-for-musl-based-systems.patch \
           "
SRC_URI[md5sum] = "884388589fb38c2109ad9fed328be20a"
SRC_URI[sha256sum] = "0a3608b17c153960cb1d954ca3b62445a77c0c1a18aa5c8c58aba9f6b8d62aab"

# CFLAGS isn't in EXTRA_OEMAKE, as the makefile picks it up via ?=
EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'LDFLAGS=${LDFLAGS}' \
    \
    'prefix=${prefix}' \
    'bindir=${bindir}' \
    'libdir=${libdir}' \
    'includedir=${includedir}' \
    'mandir=${mandir}' \
    'PKG_CONFIG=pkg-config' \
"

CFLAGS += "-I${STAGING_INCDIR}/bsd"

do_install () {
    oe_runmake install DESTDIR=${D}
}

inherit pkgconfig
