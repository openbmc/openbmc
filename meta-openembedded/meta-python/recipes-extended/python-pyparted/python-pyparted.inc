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
DEPENDS += "parted"

# upstream only publishes releases in github archives which are discouraged
SRCREV = "2396ba7df56de277b4999bdd5f7d1a1706a6388a"
SRC_URI = "git://github.com/rhinstaller/pyparted.git;protocol=https"

S = "${WORKDIR}/git"

RDEPENDS_${PN}_class-target += " \
    parted (>= 2.3) \
"
RDEPENDS_${PN}_class-native = ""
