SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF & BSD-2-Clause & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=fecee07ad8df9116e1f739e2ed2ea513"

SRC_URI[sha256sum] = "679987caf361a7539d76e584cbeddc311e3aee937877c87346f31debc63e9d06"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
