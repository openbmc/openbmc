SUMMARY = "dumb networking library"
HOMEPAGE = "https://github.com/ofalk/libdnet"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0036c1b155f4e999f3e0a373490b5db9"

SRC_URI = "git://github.com/ofalk/libdnet.git;nobranch=1;protocol=https"

SRCREV = "912f7848bfff4ebc6d610f802a3144f84880cbe4"

UPSTREAM_CHECK_GITTAGREGEX = "libdnet-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit autotools multilib_script pkgconfig

DEPENDS += "libcheck"

EXTRA_AUTORECONF += "-I ./config"
BBCLASSEXTEND = "native"

MULTILIB_SCRIPTS = "${PN}:${bindir}/dnet-config"
