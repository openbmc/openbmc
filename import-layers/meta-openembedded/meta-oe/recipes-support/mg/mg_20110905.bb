SUMMARY = "A portable version of the mg maintained by the OpenBSD team"
HOMEPAGE = "http://homepage.boetes.org/software/mg/"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://version.c;md5=811e1b67a5cd60c12b218a2b7c1adbf2"
DEPENDS = "ncurses"
SECTION = "console/editors"

SRC_URI = "http://homepage.boetes.org/software/mg/mg-${PV}.tar.gz \
           file://remove_ncurses_check.patch"

SRC_URI[md5sum] = "2de35316fa8ebafe6003efaae70b723e"
SRC_URI[sha256sum] = "1cd37d7e6a3eecc890a5718c38b8f38495057ba93856762a756ccee2f9618229"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_configure () {
    sed -i Makefile.in -e 's,^prefix=.*,prefix=${prefix},'
    ./configure
}

do_install () {
    oe_runmake install DESTDIR=${D}
}
