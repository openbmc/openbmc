SUMMARY = "Speech Audio Codec"
DESCRIPTION = "Speex is an Open Source/Free Software patent-free audio compression format designed for speech."
HOMEPAGE = "http://www.speex.org"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=314649d8ba9dd7045dfb6683f298d0a8 \
                    file://include/speex/speex.h;beginline=1;endline=34;md5=ef8c8ea4f7198d71cf3509c6ed05ea50"
DEPENDS = "libogg speexdsp"

SRC_URI = "http://downloads.xiph.org/releases/speex/speex-${PV}.tar.gz \
           file://CVE-2020-23903.patch \
           "
UPSTREAM_CHECK_REGEX = "speex-(?P<pver>\d+(\.\d+)+)\.tar"

SRC_URI[md5sum] = "8ab7bb2589110dfaf0ed7fa7757dc49c"
SRC_URI[sha256sum] = "eaae8af0ac742dc7d542c9439ac72f1f385ce838392dc849cae4536af9210094"

inherit autotools pkgconfig lib_package

EXTRA_OECONF = "\
        ${@bb.utils.contains('TARGET_FPU', 'soft', '--enable-fixed-point --disable-float-api --disable-vbr', '', d)} \
"
