SUMMARY = "Speech Audio Codec"
DESCRIPTION = "Speex is an Open Source/Free Software patent-free audio compression format designed for speech."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=314649d8ba9dd7045dfb6683f298d0a8 \
                    file://include/speex/speex.h;beginline=1;endline=34;md5=ef8c8ea4f7198d71cf3509c6ed05ea50"
DEPENDS = "libogg speexdsp"

SRC_URI = "http://downloads.us.xiph.org/releases/speex/speex-${PV}.tar.gz"

SRC_URI[md5sum] = "6ae7db3bab01e1d4b86bacfa8ca33e81"
SRC_URI[sha256sum] = "caa27c7247ff15c8521c2ae0ea21987c9e9710a8f2d3448e8b79da9806bce891"

inherit autotools pkgconfig lib_package

EXTRA_OECONF = "\
        ${@bb.utils.contains('TARGET_FPU', 'soft', '--enable-fixed-point --disable-float-api --disable-vbr', '', d)} \
"
