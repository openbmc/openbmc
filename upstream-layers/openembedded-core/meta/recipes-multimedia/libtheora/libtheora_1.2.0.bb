SUMMARY = "Theora Video Codec"
DESCRIPTION = "The libtheora reference implementation provides the standard encoder and decoder under a BSD license."
HOMEPAGE = "http://xiph.org/"
BUGTRACKER = "https://trac.xiph.org/newticket"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf91718f59eb6a83d06dc7bcaf411132"
DEPENDS = "libogg"

SRC_URI = "http://downloads.xiph.org/releases/theora/libtheora-${PV}.tar.xz \
           file://0001-add-missing-files.patch"

SRC_URI[sha256sum] = "ebdf77a8f5c0a8f7a9e42323844fa09502b34eb1d1fece7b5f54da41fe2122ec"

UPSTREAM_CHECK_REGEX = "libtheora-(?P<pver>\d+(\.\d)+)\.(tar\.gz|tgz)"

CVE_PRODUCT = "theora"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-examples --disable-doc"

# theora 1.2.0 has broken 32-bit arm assembler, see:
# https://gitlab.xiph.org/xiph/theora/-/issues/2339
# https://gitlab.xiph.org/xiph/theora/-/issues/2340
EXTRA_OECONF:append:arm = " --disable-asm"

# these old architectures don't support all the instructions from the asm source files
EXTRA_OECONF:append:armv4 = " --disable-asm "
EXTRA_OECONF:append:armv5 = " --disable-asm "
