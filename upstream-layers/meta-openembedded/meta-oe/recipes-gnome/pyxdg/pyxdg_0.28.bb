DESCRIPTION = "A Python module to deal with freedesktop.org specifications"
HOMEPAGE = "http://freedesktop.org/wiki/Software/pyxdg"
SECTION = "devel/python"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f30a9716ef3762e3467a2f62bf790f0a"

PV .= "+git"

SRCREV = "63033ac306aa26d32e1439417e59ae8f8a4c9820"
SRC_URI = "git://gitlab.freedesktop.org/xdg/pyxdg.git;branch=master;protocol=https"

inherit setuptools3

