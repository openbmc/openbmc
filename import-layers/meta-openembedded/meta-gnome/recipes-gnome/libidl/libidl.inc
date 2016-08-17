DESCRIPTION = "Library for parsing CORBA IDL files"
SECTION = "x11/gnome/libs"
LICENSE = "LGPLv2+"

inherit autotools pkgconfig

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/libIDL/0.8/libIDL-${PV}.tar.bz2"

S = "${WORKDIR}/libIDL-${PV}"

FILES_${PN} = "${libdir}/*.so.*"
FILES_${PN}-dev += " ${bindir}"
