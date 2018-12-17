SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

inherit pypi

PYPI_PACKAGE = "gitdb2"

SRC_URI[md5sum] = "6e21f5795a204f7afecb0a72fff66932"
SRC_URI[sha256sum] = "bb4c85b8a58531c51373c89f92163b92f30f81369605a67cd52d1fc21246c044"

DEPENDS = "${PYTHON_PN}-async ${PYTHON_PN}-setuptools-native ${PYTHON_PN}-smmap"

RDEPENDS_${PN} += "${PYTHON_PN}-async \
                   ${PYTHON_PN}-compression \
                   ${PYTHON_PN}-crypt \
                   ${PYTHON_PN}-io \
                   ${PYTHON_PN}-mmap \
                   ${PYTHON_PN}-shell \
                   ${PYTHON_PN}-smmap \
"
BBCLASSEXTEND = "nativesdk"
