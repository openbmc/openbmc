SUMMARY = "dumb networking library"
HOMEPAGE = "http://code.google.com/p/libdnet/"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0036c1b155f4e999f3e0a373490b5db9"

SRC_URI = "git://github.com/dugsong/libdnet.git;nobranch=1"
SRCREV = "12fca29a6d4e99d1b923d6820887fe7b24226904"

S = "${WORKDIR}/git"

inherit autotools

acpaths = "-I ./config/"

BBCLASSEXTEND = "native"
