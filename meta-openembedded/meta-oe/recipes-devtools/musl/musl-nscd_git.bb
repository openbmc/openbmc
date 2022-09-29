# Copyright (C) 2020 Armin Kuster <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Musl-nscd is an implementation of the NSCD protocol, suitable for use with musl and with standard NSS modules"
HOMEPAGE = "https://github.com/pikhq/musl-nscd"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fff9baeb9a392879d7fb25ba3a2696e4"
SECTION = "utils"

DEPENDS += "flex-native bison-native flex bison"

PV = "1.1.0"

SRCREV = "cddd6be6c629ca96f2d2e74ee52daf12bbef1f83"
SRC_URI = "git://github.com/pikhq/musl-nscd;branch=master;protocol=https \
           file://0001-nsswitch.y-Replace-empty-bison-extension.patch \
          "

UPSTREAM_CHECK_COMMITS = "1"

inherit autotools-brokensep

S = "${WORKDIR}/git"

do_configure () {
    # no debug set -s flag
    sed -i -e 's/LDFLAGS_AUTO=-s/LDFLAGS_AUTO=/' ${S}/configure
    ${S}/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

do_compile () {
    oe_runmake
}

do_install () {
    make DESTDIR=${D} install
}

COMPATIBLE_HOST = ".*-musl.*"
