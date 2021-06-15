SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e64a29fcd3c3dd356a24e235dfcb3905"

DEPENDS += "libusb1 ${PYTHON_PN}-setuptools-scm-native"

SRC_URI[sha256sum] = "7d449ad916ce58aff60b89aae0b65ac130f289c24d6a5b7b317742eccffafc38"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
