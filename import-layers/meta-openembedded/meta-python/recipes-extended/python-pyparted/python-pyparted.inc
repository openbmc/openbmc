DESCRIPTION = "pyparted is a set of Python modules that provide Python programmers \
an interface to libparted, the GNU parted library for disk partitioning and \
filesystem manipulation."
SUMMARY = "Python bindings for libparted"
HOMEPAGE = "https://github.com/rhinstaller/pyparted"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
    file://src/_pedmodule.c;startline=10;endline=22;md5=ebcb25dde1ac9c46e986dec79b404e7e \
"
DEPENDS += "parted"

# upstream only publishes releases in github archives which are discouraged
SRCREV = "69c4a478e43d3eff6ae3a0116fa38da06776d5f5"
SRC_URI = "git://github.com/rhinstaller/pyparted.git;protocol=https \
           file://0001-fix-version-float-check.patch \
"
S = "${WORKDIR}/git"

RDEPENDS_${PN} += "parted (>= 2.3)"
RDEPENDS_${PN}_class-native = ""
