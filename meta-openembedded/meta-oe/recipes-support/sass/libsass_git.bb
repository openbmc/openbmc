SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=master"
SRCREV = "8d312a1c91bb7dd22883ebdfc829003f75a82396"
PV = "3.6.4"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
