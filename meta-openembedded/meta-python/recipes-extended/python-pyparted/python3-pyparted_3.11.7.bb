DESCRIPTION = "pyparted is a set of Python modules that provide Python programmers \
an interface to libparted, the GNU parted library for disk partitioning and \
filesystem manipulation."
SUMMARY = "Python bindings for libparted"
HOMEPAGE = "https://github.com/rhinstaller/pyparted"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
    file://src/_pedmodule.c;beginline=10;endline=22;md5=9e53304db812b80d0939e11bb69dcab2 \
"

SRC_URI[md5sum] = "69284f943982f54ded76960c92098a02"
SRC_URI[sha256sum] = "61cde7b096bccd69ddc75c85b17f8baed45c3687d404706d91403a319453b903"

inherit pypi distutils3

DEPENDS += "parted"

RDEPENDS_${PN}_class-target += " \
    parted (>= 2.3) \
    python3-stringold python3-codecs python3-math \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
