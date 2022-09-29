SUMMARY = "Speech Audio Codec"
DESCRIPTION = "Speex is an Open Source/Free Software patent-free audio compression format designed for speech."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=eff3f76350f52a99a3df5eec6b79c02a \
                    file://include/speex/speex.h;beginline=1;endline=34;md5=ef8c8ea4f7198d71cf3509c6ed05ea50 \
                    "
DEPENDS = "libogg speexdsp"

SRC_URI = "http://downloads.xiph.org/releases/speex/speex-${PV}.tar.gz"
UPSTREAM_CHECK_REGEX = "speex-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[sha256sum] = "4b44d4f2b38a370a2d98a78329fefc56a0cf93d1c1be70029217baae6628feea"

inherit autotools pkgconfig lib_package

EXTRA_OECONF = "\
        ${@bb.utils.contains('TARGET_FPU', 'soft', '--enable-fixed-point --disable-float-api --disable-vbr', '', d)} \
"
