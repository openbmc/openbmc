SUMMARY = "Ogg Vorbis Audio Codec"
DESCRIPTION = "Ogg Vorbis is a high-quality lossy audio codec \
that is free of intellectual property restrictions. libvorbis \
is the main vorbis codec library."
HOMEPAGE = "https://xiph.org/vorbis/"
BUGTRACKER = "https://gitlab.xiph.org/xiph/vorbis/-/issues"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=73d9c8942c60b846c3bad13cc6c2e520 \
                    file://include/vorbis/vorbisenc.h;beginline=1;endline=11;md5=c95a4ac2b4125f00a9acf61449ebb843"
DEPENDS = "libogg"

SRC_URI = "http://downloads.xiph.org/releases/vorbis/${BP}.tar.xz \
           file://0001-configure-Check-for-clang.patch \
          "
SRC_URI[md5sum] = "50902641d358135f06a8392e61c9ac77"
SRC_URI[sha256sum] = "b33cc4934322bcbf6efcbacf49e3ca01aadbea4114ec9589d1b1e9d20f72954b"

inherit autotools pkgconfig
