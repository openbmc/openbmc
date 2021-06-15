SUMMARY = "A Python implementation of Google's Encoded Polyline Algorithm Format"
HOMEPAGE = "https://pypi.org/project/polyline/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fbd13500cabd06dd751ba6b2be304c6"

SRC_URI[md5sum] = "b97c57378605c4a856c437569f95a0cb"
SRC_URI[sha256sum] = "7c7f89d09a09c7b6161bdbfb4fd304b186fc7a2060fa4f31cb3f61c646a5c074"

inherit pypi setuptools3 ptest

RDEPENDS_${PN} += "python3-six"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/test
	cp -rf ${S}/test/* ${D}${PTEST_PATH}/test/
}
