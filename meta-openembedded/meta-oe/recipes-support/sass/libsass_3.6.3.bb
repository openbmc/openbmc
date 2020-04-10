SUMMARY = "C/C++ port of the Sass CSS precompiler"
HOMEPAGE = "http://sass-lang.com/libsass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f34396ca205f5e119ee77aae91fa27d"

inherit autotools

SRC_URI = "git://github.com/sass/libsass.git;branch=master"
SRCREV = "e1c16e09b4a953757a15149deaaf28a3fd81dc97"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
