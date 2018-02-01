SUMMARY = "Text processing system for documentation"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF & BSD-2-Clause & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=7a4646907ab9083c826280b19e103106"

DEPENDS = "python3"

SRC_URI = "${SOURCEFORGE_MIRROR}/docutils/docutils-${PV}.tar.gz"
SRC_URI[md5sum] = "ea4a893c633c788be9b8078b6b305d53"
SRC_URI[sha256sum] = "718c0f5fb677be0f34b781e04241c4067cbd9327b66bdd8e763201130f5175be"

S = "${WORKDIR}/docutils-${PV}"

inherit distutils3

BBCLASSEXTEND = "native"

