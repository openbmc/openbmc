DESCRIPTION = "C++ wrapper around the nanomsg NNG API"
HOMEPAGE = "https://github.com/cwzx/nngpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=6d17d78c3597e0d4452fb1c63bf7c58e"
DEPENDS = "nng"

SRCREV = "cc5d2641babab165d8a9943817c46d36c6dc17c2"
PV = "1.3.0"

SRC_URI = "git://github.com/cwzx/nngpp"

S = "${WORKDIR}/git"

inherit cmake
