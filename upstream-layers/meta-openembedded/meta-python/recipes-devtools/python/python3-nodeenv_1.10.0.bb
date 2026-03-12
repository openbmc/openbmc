SUMMARY = "Node.js virtual environment builder"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a9e238ffae5bb6447dbac6291e1dc3a3"

PYPI_PACKAGE = "nodeenv"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "996c191ad80897d076bdfba80a41994c2b47c68e224c542b48feba42ba00f8bb"

DEPENDS += "python3-setuptools-scm-native"
