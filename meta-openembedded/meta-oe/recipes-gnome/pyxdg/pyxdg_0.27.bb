DESCRIPTION = "A Python module to deal with freedesktop.org specifications"
HOMEPAGE = "http://freedesktop.org/wiki/Software/pyxdg"
SECTION = "devel/python"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f30a9716ef3762e3467a2f62bf790f0a"

SRCREV = "f097a66923a65e93640c48da83e6e9cfbddd86ba"
SRC_URI = "git://anongit.freedesktop.org/xdg/pyxdg;branch=master"

inherit setuptools3

S = "${WORKDIR}/git"
