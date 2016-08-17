DESCRIPTION = "The Audio File Library provides a uniform and elegant \
API for accessing a variety of audio file formats, such as AIFF/AIFF-C, \
WAVE, NeXT/Sun .snd/.au, Berkeley/IRCAM/CARL Sound File, Audio Visual \
Research, Amiga IFF/8SVX, and NIST SPHERE."
HOMEPAGE = "http://www.68k.org/~michael/audiofile/"
SECTION = "libs"
LICENSE = "LGPLv2 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://COPYING.GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "${GNOME_MIRROR}/audiofile/0.2/${BP}.tar.gz"
SRC_URI[md5sum] = "a39be317a7b1971b408805dc5e371862"
SRC_URI[sha256sum] = "a61c4036c2600a645843f16bec4be166093a9df5f15b02c85291213aa9cf15a2"

inherit autotools lib_package binconfig
