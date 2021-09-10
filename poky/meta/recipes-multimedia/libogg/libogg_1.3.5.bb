SUMMARY = "Ogg bitstream and framing libary"
DESCRIPTION = "libogg is the bitstream and framing library \
for the Ogg project. It provides functions which are \
necessary to codec libraries like libvorbis."
HOMEPAGE = "http://xiph.org/"
BUGTRACKER = "https://trac.xiph.org/newticket"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=db1b7a668b2a6f47b2af88fb008ad555 \
                    file://include/ogg/ogg.h;beginline=1;endline=11;md5=eda812856f13a3b1326eb8f020cc3b0b"

SRC_URI = "http://downloads.xiph.org/releases/ogg/${BP}.tar.xz"

SRC_URI[md5sum] = "3178c98341559657a15b185bf5d700a5"
SRC_URI[sha256sum] = "c4d91be36fc8e54deae7575241e03f4211eb102afb3fc0775fbbc1b740016705"

inherit autotools pkgconfig
