SUMMARY = "A pure-Python git object database"
HOMEPAGE = "http://github.com/gitpython-developers/gitdb"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=59e5ecb13339a936eedf83282eaf4528"

inherit pypi

PYPI_PACKAGE = "gitdb2"

SRC_URI[md5sum] = "d5217eb94ebd36fcec62b929d1f72b00"
SRC_URI[sha256sum] = "b60e29d4533e5e25bb50b7678bbc187c8f6bcff1344b4f293b2ba55c85795f09"

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
