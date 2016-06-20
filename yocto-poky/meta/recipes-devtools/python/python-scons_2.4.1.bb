SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=23bc1530c55e9f0d1b709056bcea237d"
SRCNAME = "scons"

SRC_URI = "${SOURCEFORGE_MIRROR}/scons/scons-${PV}.tar.gz"

SRC_URI[md5sum] = "9a0ddf33d9839f04380e0fae87cc4b40"
SRC_URI[sha256sum] = "8fc4f42928c69bcbb33e1be94b646f2c700b659693fabc778c192d4d22f753a7"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/scons/files/scons/"
UPSTREAM_CHECK_REGEX = "/scons/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

RDEPENDS_${PN} = "\
  python-fcntl \
  python-io \
  "
