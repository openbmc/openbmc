SUMMARY = "LibHTP is a security-aware parser for the HTTP protocol and the related bits and pieces."

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=596ab7963a1a0e5198e5a1c4aa621843"

SRC_URI = "git://github.com/OISF/libhtp.git;protocol=https;branch=0.5.x \
           file://CVE-2024-45797.patch \
          "
SRCREV = "8bdfe7b9d04e5e948c8fbaa7472e14d884cc00af"

DEPENDS = "zlib"

inherit autotools-brokensep pkgconfig

CFLAGS += "-D_DEFAULT_SOURCE"

#S = "${UNPACKDIR}/suricata-${VER}/${BPN}"

S = "${UNPACKDIR}/git"

do_configure () {
    cd ${S}
    ./autogen.sh
    oe_runconf
}

RDEPENDS:${PN} += "zlib"

