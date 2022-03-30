SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fdfc019b57affbe1d7a32e3d34e83db4"

SRC_URI[sha256sum] = "8323a43bd9c91f62bb7d4be74cc9ff10090e7ef820e27bfe8815c57e68261311"

inherit pypi setuptools3 

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native"
