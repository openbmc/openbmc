SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=master"
SRCREV = "f6afdbb9288d20d1257122e71d88e53348a53af3"
PV = "3.6.5"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
