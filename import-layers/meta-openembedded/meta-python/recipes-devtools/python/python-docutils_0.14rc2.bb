SUMMARY = "Text processing system for documentation"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF & BSD-2-Clause & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=35a23d42b615470583563132872c97d6"

DEPENDS = "python"

SRC_URI = "${SOURCEFORGE_MIRROR}/docutils/docutils-${PV}.tar.gz"
SRC_URI[md5sum] = "2f4bee6451958252f7bec71f18b05be3"
SRC_URI[sha256sum] = "3caee0bcb2a49fdf24fcfa70849a60abb7a181aa68b030f7cb7494096181830c"

S = "${WORKDIR}/docutils-${PV}"

inherit distutils

BBCLASSEXTEND = "native"

