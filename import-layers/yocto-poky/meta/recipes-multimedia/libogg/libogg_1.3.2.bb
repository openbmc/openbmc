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

SRC_URI[md5sum] = "5c3a34309d8b98640827e5d0991a4015"
SRC_URI[sha256sum] = "3f687ccdd5ac8b52d76328fbbfebc70c459a40ea891dbf3dccb74a210826e79b"

inherit autotools pkgconfig
