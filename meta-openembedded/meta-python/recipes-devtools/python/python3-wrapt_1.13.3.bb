SUMMARY = "A Python module for decorators, wrappers and monkey patching."
HOMEPAGE = "http://wrapt.readthedocs.org/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fdfc019b57affbe1d7a32e3d34e83db4"

SRC_URI[sha256sum] = "1fea9cd438686e6682271d36f3481a9f3636195578bab9ca3382e2f5f01fc185"

inherit pypi setuptools3 

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native"
