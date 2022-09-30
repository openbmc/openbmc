SUMMARY = "ANTLR runtime for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9a0a1d20e667cf7ab7c59357cf8b4812"

SRC_URI[sha256sum] = "a53de701312f9bdacc5258a6872cd6c62b90d3a90ae25e494026f76267333b60"

PYPI_PACKAGE = "antlr4-python3-runtime"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "nativesdk native"
