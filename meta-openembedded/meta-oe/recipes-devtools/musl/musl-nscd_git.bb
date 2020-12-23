# Copyright (C) 2020 Armin Kuster <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Musl-nscd is an implementation of the NSCD protocol, suitable for use with musl and with standard NSS modules"
HOMEPAGE = "https://github.com/pikhq/musl-nscd"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=9bf479a145bcaff8489e743da58afeee"
SECTION = "utils"

DEPENDS += "flex-native bison-native flex bison"

PV = "1.0.2"

SRCREV = "af581482a3e1059458f3c8b20a56f82807ca3bd4"
SRC_URI = "git://github.com/pikhq/musl-nscd \
           file://0001-Fix-build-under-GCC-fno-common.patch \
           file://0001-configure-Check-for-flex-if-lex-is-not-found.patch \
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
