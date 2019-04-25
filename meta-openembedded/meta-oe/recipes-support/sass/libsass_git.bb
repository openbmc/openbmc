SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=3.5-stable"
SRCREV = "39e30874b9a5dd6a802c20e8b0470ba44eeba929"
S = "${WORKDIR}/git"
PV = "3.5.5"

BBCLASSEXTEND = "native"
