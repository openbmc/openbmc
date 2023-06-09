DESCRIPTION = "pyparted is a set of Python modules that provide Python programmers \
an interface to libparted, the GNU parted library for disk partitioning and \
filesystem manipulation."
SUMMARY = "Python bindings for libparted"
HOMEPAGE = "https://github.com/rhinstaller/pyparted"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
    file://src/_pedmodule.c;beginline=10;endline=22;md5=9e53304db812b80d0939e11bb69dcab2 \
"

SRC_URI[sha256sum] = "da985e116beb733371feb605b174db9eec8bd0eedffc8f739f8e603f51b521e7"

inherit pkgconfig pypi setuptools3

DEPENDS += "parted"

RDEPENDS:${PN}:class-target += " \
    parted (>= 2.3) \
    python3-codecs \
    python3-math \
    python3-numbers \
    python3-stringold \
"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native"
