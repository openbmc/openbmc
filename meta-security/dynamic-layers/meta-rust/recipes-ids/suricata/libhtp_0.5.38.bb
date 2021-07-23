SUMMARY = "LibHTP is a security-aware parser for the HTTP protocol and the related bits and pieces."

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=596ab7963a1a0e5198e5a1c4aa621843"

SRC_URI = "git://github.com/OISF/libhtp.git;protocol=https;branch=0.5.x"
SRCREV = "fca44158911a1642880ea5c774151a33ad33d906"

DEPENDS = "zlib"

inherit autotools-brokensep pkgconfig

CFLAGS += "-D_DEFAULT_SOURCE"

#S = "${WORKDIR}/suricata-${VER}/${BPN}"

S = "${WORKDIR}/git"

do_configure () {
    cd ${S}
    ./autogen.sh
    oe_runconf
}

RDEPENDS_${PN} += "zlib"

