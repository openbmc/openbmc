SUMMARY = "GTK Performance tool"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://prdownloads.sourceforge.net/${BPN}/${BPN}_${PV}.tar.gz \
           file://Makevars \
           file://0001-Include-stdlib.h-for-exit-API.patch \
           file://0002-timing.c-Fix-format-security-errors.patch \
           file://gettext.patch \
           file://0001-main.c-Define-prototype-for-setup_appdata.patch \
           "

SRC_URI[md5sum] = "4331dde4bb83865e15482885fcb0cc53"
SRC_URI[sha256sum] = "9704344e732038eecbd007dd996a56293a6b027b5b76f3f036273a3fae1ab27b"

DEPENDS = "gtk+"

S = "${WORKDIR}/${BPN}"

inherit features_check autotools binconfig pkgconfig gettext

REQUIRED_DISTRO_FEATURES = "x11"

do_configure:prepend () {
    rm -f ${S}/m4/init.m4
    cp -f ${UNPACKDIR}/Makevars ${S}/po/
}

do_install:append () {
    rm -rf ${D}/${exec_prefix}/doc
}

FILES:${PN} += "${exec_prefix}/share/duck.png"
