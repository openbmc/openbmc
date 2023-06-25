SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "PSF-2.0 & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=08f5f8aa6a1db2500c08a2bb558e45af"

SRC_URI[sha256sum] = "f08a4e276c3a1583a86dce3e34aba3fe04d02bba2dd51ed16106244e8a923e3b"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
