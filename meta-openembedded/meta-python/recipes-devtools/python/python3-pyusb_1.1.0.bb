SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c52a96fd9a0cadcb9270904c8eb5416c"
DEPENDS += "libusb1 ${PYTHON_PN}-setuptools-scm-native"

SRC_URI[md5sum] = "3b2e38e9f697d2f90d86376bd10a9505"
SRC_URI[sha256sum] = "d69ed64bff0e2102da11b3f49567256867853b861178689671a163d30865c298"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
