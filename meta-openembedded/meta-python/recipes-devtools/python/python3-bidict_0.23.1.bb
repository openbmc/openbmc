SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e907308cc9356afa99ac0eec6b20211b"

SRC_URI[sha256sum] = "03069d763bc387bbd20e7d49914e75fc4132a41937fa3405417e1a5a2d006d71"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
