DESCRIPTION = "The Audio File Library provides a uniform and elegant \
API for accessing a variety of audio file formats, such as AIFF/AIFF-C, \
WAVE, NeXT/Sun .snd/.au, Berkeley/IRCAM/CARL Sound File, Audio Visual \
Research, Amiga IFF/8SVX, and NIST SPHERE."
HOMEPAGE = "http://www.68k.org/~michael/audiofile/"
SECTION = "libs"
LICENSE = "LGPL-2.0-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    ${GNOME_MIRROR}/audiofile/0.3/${BP}.tar.xz \
    file://0001-fix-negative-shift-constants.patch \
    file://0002-fix-build-on-gcc6.patch \
    file://0003-fix-CVE-2015-7747.patch \
    file://0004-Always-check-the-number-of-coefficients.patch \
    file://0005-clamp-index-values-to-fix-index-overflow-in-IMA.cpp.patch \
    file://0006-Check-for-multiplication-overflow-in-sfconvert.patch \
    file://0007-Actually-fail-when-error-occurs-in-parseFormat.patch \
    file://0008-Check-for-multiplication-overflow-in-MSADPCM-decodeS.patch \
"
SRC_URI[sha256sum] = "ea2449ad3f201ec590d811db9da6d02ffc5e87a677d06b92ab15363d8cb59782"

inherit autotools lib_package pkgconfig

CXXFLAGS += "-std=c++14"

DEPENDS = " \
    asciidoc-native \
    alsa-lib \
    libogg \
    flac \
"
