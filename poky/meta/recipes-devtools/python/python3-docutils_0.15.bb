SUMMARY = "Text processing system for documentation"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF & BSD-2-Clause & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=35a23d42b615470583563132872c97d6"

DEPENDS = "python3"

SRC_URI = "${SOURCEFORGE_MIRROR}/docutils/docutils-${PV}.tar.gz"
SRC_URI[md5sum] = "f51729f19e70a9dc4837433193a5e798"
SRC_URI[sha256sum] = "c35e87e985f70106f6f97e050f3bed990641e0e104566134b9cd23849a460e96"

S = "${WORKDIR}/docutils-${PV}"

inherit distutils3

BBCLASSEXTEND = "native"
