SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

inherit pypi

PYPI_PACKAGE = "gitdb"

SRC_URI[sha256sum] = "96bf5c08b157a666fec41129e6d327235284cca4c81e92109260f353ba138005"

DEPENDS = "${PYTHON_PN}-async ${PYTHON_PN}-setuptools-native ${PYTHON_PN}-smmap"

RDEPENDS:${PN} += "${PYTHON_PN}-async \
                   ${PYTHON_PN}-compression \
                   ${PYTHON_PN}-crypt \
                   ${PYTHON_PN}-io \
                   ${PYTHON_PN}-mmap \
                   ${PYTHON_PN}-shell \
                   ${PYTHON_PN}-smmap \
"
BBCLASSEXTEND = "native nativesdk"
