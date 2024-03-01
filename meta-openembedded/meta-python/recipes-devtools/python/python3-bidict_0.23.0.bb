SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e907308cc9356afa99ac0eec6b20211b"

SRC_URI[sha256sum] = "3959ca59d4d6997702d642bf1e5fd93cba299863723fc289545198f70c468578"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
