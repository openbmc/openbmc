SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=master"
SRCREV = "4d229af5500be1023883c38c4a675f0ed919839d"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
