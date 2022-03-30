SUMMARY = "ATA S.M.A.R.T. Reading and Parsing Library"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "udev"

SRCREV = "de6258940960443038b4c1651dfda3620075e870"
SRC_URI = "git://git.0pointer.de/libatasmart.git;branch=master \
           file://0001-Makefile.am-add-CFLAGS-and-LDFLAGS-definiton.patch \
"

S = "${WORKDIR}/git"

inherit autotools lib_package pkgconfig

do_install:append() {
    sed -i -e s://:/:g -e 's:=${libdir}/libudev.la:-ludev:g' ${D}${libdir}/libatasmart.la
}

PACKAGES =+ "${PN}-dev-vala"
FILES:${PN}-dev-vala = "${datadir}/vala"
