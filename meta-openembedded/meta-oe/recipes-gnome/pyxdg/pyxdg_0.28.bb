DESCRIPTION = "A Python module to deal with freedesktop.org specifications"
HOMEPAGE = "http://freedesktop.org/wiki/Software/pyxdg"
SECTION = "devel/python"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f30a9716ef3762e3467a2f62bf790f0a"

SRCREV = "1d23e483ae869ee9532aca43b133cc43f63626a3"
SRC_URI = "git://anongit.freedesktop.org/xdg/pyxdg;branch=master"

inherit setuptools3

S = "${WORKDIR}/git"
