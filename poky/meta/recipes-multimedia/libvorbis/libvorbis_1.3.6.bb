SUMMARY = "Ogg Vorbis Audio Codec"
DESCRIPTION = "Ogg Vorbis is a high-quality lossy audio codec \
that is free of intellectual property restrictions. libvorbis \
is the main vorbis codec library."
HOMEPAGE = "http://www.vorbis.com/"
BUGTRACKER = "https://trac.xiph.org"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=70c7063491d2d9f76a098d62ed5134f1 \
                    file://include/vorbis/vorbisenc.h;beginline=1;endline=11;md5=d1c1d138863d6315131193d4046d81cb"
DEPENDS = "libogg"

SRC_URI = "http://downloads.xiph.org/releases/vorbis/${BP}.tar.xz \
           file://0001-configure-Check-for-clang.patch \
           file://CVE-2018-10392.patch \
           file://CVE-2017-14160.patch \
          "
SRC_URI[md5sum] = "b7d1692f275c73e7833ed1cc2697cd65"
SRC_URI[sha256sum] = "af00bb5a784e7c9e69f56823de4637c350643deedaf333d0fa86ecdba6fcb415"

inherit autotools pkgconfig
