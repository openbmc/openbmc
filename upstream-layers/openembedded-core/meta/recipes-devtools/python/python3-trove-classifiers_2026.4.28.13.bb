SUMMARY = "Canonical source for classifiers on PyPI (pypi.org)."
HOMEPAGE = "https://github.com/pypa/trove-classifiers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "c85bb8a53c3de7330d1699b844ed9fb809a602a09ac15dc79ad6d1a509be0676"

PYPI_PACKAGE = "trove_classifiers"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += " python3-calver-native"

BBCLASSEXTEND = "native nativesdk"
