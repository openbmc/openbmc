SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=858e74278ef3830d46481172ae03c819"

SRC_URI[sha256sum] = "5c826b3e15e97cc6e615de295756847c282a79b79c5430d3bfc909b1ac9f5bd8"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
