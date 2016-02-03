SUMMARY = "Python lightweight in-process concurrent programming"
HOMEPAGE = "http://pypi.python.org/pypi/greenlet"
SECTION = "devel/python"
LICENSE = "MIT & PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03143d7a1a9f5d8a0fee825f24ca9c36 \
                    file://LICENSE.PSF;md5=c106931d9429eda0492617f037b8f69a"
SRC_URI = "http://pypi.python.org/packages/source/g/greenlet/greenlet-${PV}.zip"
SRC_URI[md5sum] = "c2deda75bdda59c38cae12a77cc53adc"
SRC_URI[sha256sum] = "ea671592f8460541286b133ed46a6cf5311a6b75051cc31b53e2bc38992b775a"

S = "${WORKDIR}/greenlet-${PV}"

inherit distutils

