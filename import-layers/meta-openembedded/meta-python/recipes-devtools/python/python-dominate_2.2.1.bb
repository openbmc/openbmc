SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=a30b9a8d0a5585c482c6c5a6d54aaebc"

SRC_URI[md5sum] = "cd156b5b290f49f2bb5814ea8acc12a3"
SRC_URI[sha256sum] = "4aa6a2f458461541f9ceeb58b49da9b42320f80aa2a6f692baea2817431d9953"

PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    "
