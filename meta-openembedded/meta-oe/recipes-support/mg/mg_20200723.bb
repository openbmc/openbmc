SUMMARY = "A portable version of the mg maintained by the OpenBSD team"
HOMEPAGE = "http://homepage.boetes.org/software/mg/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://version.c;md5=1895eb37bf6bd79cdc5c89d8166fabfb"
DEPENDS = "ncurses libbsd"
SECTION = "console/editors"

SRCREV = "60fa3582f5f735b9d43825f5bdcc4ea5f0740f91"
SRC_URI = "git://github.com/hboetes/mg \
           file://0001-fileio-Include-sys-param.h-for-MAXNAMLEN.patch \
           file://0002-fileio-Define-DEFFILEMODE-if-platform-is-missing.patch \
           "
SRC_URI_append_libc-musl = "\
           file://0001-Undefine-REGEX-for-musl-based-systems.patch \
           "

S = "${WORKDIR}/git"

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
