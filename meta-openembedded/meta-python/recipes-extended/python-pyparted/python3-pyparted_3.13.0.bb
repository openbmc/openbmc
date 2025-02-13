DESCRIPTION = "pyparted is a set of Python modules that provide Python programmers \
an interface to libparted, the GNU parted library for disk partitioning and \
filesystem manipulation."
SUMMARY = "Python bindings for libparted"
HOMEPAGE = "https://github.com/rhinstaller/pyparted"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=8ca43cbc842c2336e835926c2166c28b \
    file://src/_pedmodule.c;beginline=10;endline=22;md5=c4b9f810cd53b16ff269447fb8af6c3d \
"

SRC_URI[sha256sum] = "443b59eb9ac63b8ca87094e02376646e172c7ea075f955f105889ca3485b06fd"

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
