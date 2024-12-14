SUMMARY = "Common functions for SBLIM Small Footprint CIM Broker and CIM Client Library."
DESCRIPTION = "\
This package provides a common library for functions shared between Small Footprint CIM Broker (sblim-sfcb) \
Small Footprint CIM Client (and sblim-sfcc)."
HOMEPAGE = "http://sourceforge.net/projects/sblim/"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f300afd598546add034364cd0a533261"
SECTION = "Development/Libraries"

DEPENDS = "cmpi-bindings"

S = "${WORKDIR}/sblim-sfcCommon-${PV}"
SRC_URI = "http://downloads.sourceforge.net/sblim/sblim-sfcCommon-${PV}.tar.bz2"
SRC_URI[sha256sum] = "b9b1037173d6ae0181c3bd5a316ddab5afd6a342ad0dbdc18e940fc0ad2c3297"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/sblim/files/sblim-sfcCommon/"

inherit autotools

do_install() {
    oe_runmake DESTDIR=${D} install

    rm -rf ${D}${libdir}/libsfcUtil.a
    rm -rf ${D}${libdir}/libsfcUtil.la
}

