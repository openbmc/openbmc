SUMMARY = "dumb networking library"
HOMEPAGE = "http://code.google.com/p/libdnet/"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0036c1b155f4e999f3e0a373490b5db9"

SRC_URI = "git://github.com/dugsong/libdnet.git;nobranch=1"
SRCREV = "3e782472d2a58d5e1b94d04eda4a364c2d257600"

UPSTREAM_CHECK_GITTAGREGEX = "libdnet-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit autotools multilib_script

acpaths = "-I ./config/"

BBCLASSEXTEND = "native"

MULTILIB_SCRIPTS = "${PN}:${bindir}/dnet-config"
