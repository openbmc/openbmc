SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF-2.0 & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=41001b296a89bb2780bbe306e947ecee"

SRC_URI[sha256sum] = "33995a6753c30b7f577febfc2c50411fec6aac7f7ffeb7c4cfe5991072dcf9e6"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
