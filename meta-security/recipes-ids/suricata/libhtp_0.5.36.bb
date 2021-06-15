SUMMARY = "LibHTP is a security-aware parser for the HTTP protocol and the related bits and pieces."

require suricata.inc

LIC_FILES_CHKSUM = "file://../LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

DEPENDS = "zlib"

inherit autotools pkgconfig

CFLAGS += "-D_DEFAULT_SOURCE"

S = "${WORKDIR}/suricata-${VER}/${BPN}"

RDEPENDS_${PN} += "zlib"
