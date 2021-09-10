DESCRIPTION = "Backports and enhancements for the contextlib module"
HOMEPAGE = "http://contextlib2.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=43d1c7827e8fad6454b553caf0e1d734"

SRC_URI[sha256sum] = "01f490098c18b19d2bd5bb5dc445b2054d2fa97f09a4280ba2c5f3c394c8162e"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
