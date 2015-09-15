SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=23bc1530c55e9f0d1b709056bcea237d"
SRCNAME = "scons"

SRC_URI = "${SOURCEFORGE_MIRROR}/scons/scons-${PV}.tar.gz"

SRC_URI[md5sum] = "9c6a1c3c716cbe5f16515f163c91d928"
SRC_URI[sha256sum] = "98adaa351d8f4e4068a5bf1894bdd7f85b390c8c3f80d437cf8bb266012404df"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils
