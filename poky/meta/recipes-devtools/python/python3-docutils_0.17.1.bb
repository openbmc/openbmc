SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF & BSD-2-Clause & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=836a1950177996968a49ff477a4a61c4"

SRC_URI[sha256sum] = "686577d2e4c32380bb50cbb22f575ed742d58168cee37e99117a854bcd88f125"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
