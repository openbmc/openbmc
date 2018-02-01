DESCRIPTION = "The Audio File Library provides a uniform and elegant \
API for accessing a variety of audio file formats, such as AIFF/AIFF-C, \
WAVE, NeXT/Sun .snd/.au, Berkeley/IRCAM/CARL Sound File, Audio Visual \
Research, Amiga IFF/8SVX, and NIST SPHERE."
HOMEPAGE = "http://www.68k.org/~michael/audiofile/"
SECTION = "libs"
LICENSE = "LGPLv2 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    ${GNOME_MIRROR}/audiofile/0.3/${BP}.tar.xz \
    file://0001-fix-negative-shift-constants.patch \
    file://0002-fix-build-on-gcc6.patch \
    file://0003-fix-CVE-2015-7747.patch \
"
SRC_URI[md5sum] = "235dde14742317328f0109e9866a8008"
SRC_URI[sha256sum] = "ea2449ad3f201ec590d811db9da6d02ffc5e87a677d06b92ab15363d8cb59782"

inherit autotools lib_package pkgconfig

DEPENDS = " \
    asciidoc-native \
    alsa-lib \
    libogg \
    flac \
"
