SUMMARY = "Ogg bitstream and framing libary"
DESCRIPTION = "libogg is the bitstream and framing library \
for the Ogg project. It provides functions which are \
necessary to codec libraries like libvorbis."
HOMEPAGE = "http://xiph.org/"
BUGTRACKER = "https://trac.xiph.org/newticket"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=db1b7a668b2a6f47b2af88fb008ad555 \
                    file://include/ogg/ogg.h;beginline=1;endline=11;md5=eda812856f13a3b1326eb8f020cc3b0b"

SRC_URI = "http://downloads.xiph.org/releases/ogg/${BP}.tar.xz"

SRC_URI[md5sum] = "87ed742047f065046eb6c36745d871b8"
SRC_URI[sha256sum] = "4f3fc6178a533d392064f14776b23c397ed4b9f48f5de297aba73b643f955c08"

inherit autotools pkgconfig
