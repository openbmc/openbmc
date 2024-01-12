SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=master;protocol=https"
SRCREV = "7037f03fabeb2b18b5efa84403f5a6d7a990f460"
PV = "3.6.6"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
