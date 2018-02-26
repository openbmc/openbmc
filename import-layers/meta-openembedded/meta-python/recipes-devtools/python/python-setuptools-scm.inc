SUMMARY = "the blessed package to manage your versions by scm tags"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=838c366f69b72c5df05c96dff79b35f2"

SRC_URI[md5sum] = "f17493d53f0d842bb0152f214775640b"
SRC_URI[sha256sum] = "49ab4685589986a42da85706b3311a2f74f1af567d39fee6cb1e088d7a75fb5f"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi

RDEPENDS_${PN}_class-target = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-argparse ${PYTHON_PN}-debugger ${PYTHON_PN}-json"
RDEPENDS_${PN}_class-native = "${PYTHON_PN}-setuptools-native"

BBCLASSEXTEND = "native"
