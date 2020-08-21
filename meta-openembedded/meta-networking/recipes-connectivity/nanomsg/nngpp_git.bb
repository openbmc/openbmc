DESCRIPTION = "C++ wrapper around the nanomsg NNG API"
HOMEPAGE = "https://github.com/cwzx/nngpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=6d17d78c3597e0d4452fb1c63bf7c58e"
DEPENDS = "nng"

SRCREV = "85294eda3f584281439649a074f46e2d3516b2a1"
PV = "1.2.4"

SRC_URI = "git://github.com/cwzx/nngpp"

S = "${WORKDIR}/git"

inherit cmake
