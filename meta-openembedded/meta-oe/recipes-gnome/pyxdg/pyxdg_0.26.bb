DESCRIPTION = "A Python module to deal with freedesktop.org specifications"
HOMEPAGE = "http://freedesktop.org/wiki/Software/pyxdg"
SECTION = "devel/python"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f30a9716ef3762e3467a2f62bf790f0a"

SRCREV = "7db14dcf4c4305c3859a2d9fcf9f5da2db328330"
SRC_URI = "git://anongit.freedesktop.org/xdg/pyxdg"

inherit distutils

S = "${WORKDIR}/git"
