SUMMARY = "A portable version of the mg maintained by the OpenBSD team"
HOMEPAGE = "http://homepage.boetes.org/software/mg/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://version.c;md5=1895eb37bf6bd79cdc5c89d8166fabfb"
DEPENDS = "ncurses libbsd"
SECTION = "console/editors"

SRC_URI = "http://homepage.boetes.org/software/mg/mg-${PV}.tar.gz"
SRC_URI[md5sum] = "fc6faeeee7308bb46f3512b75867ed51"
SRC_URI[sha256sum] = "b7fcb5136a6783ca24c8463ab0852fc1f26bdb2bb1c24759b2c51ccfc46c5e61"

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
