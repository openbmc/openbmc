SUMMARY = "ATA S.M.A.R.T. Reading and Parsing Library"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "udev"

SRCREV = "de6258940960443038b4c1651dfda3620075e870"
SRC_URI = "git://git.0pointer.de/libatasmart.git"

S = "${WORKDIR}/git"

inherit autotools lib_package pkgconfig

do_install_append() {
    sed -i -e s://:/:g -e 's:=${libdir}/libudev.la:-ludev:g' ${D}${libdir}/libatasmart.la
}

PACKAGES =+ "${PN}-dev-vala"
FILES_${PN}-dev-vala = "${datadir}/vala"
