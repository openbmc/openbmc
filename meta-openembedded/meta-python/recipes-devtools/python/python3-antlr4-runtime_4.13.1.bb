SUMMARY = "ANTLR runtime for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=4b18e4a9f93178eaa50a9a37f26da7f7"

SRC_URI[sha256sum] = "3cd282f5ea7cfb841537fe01f143350fdb1c0b1ce7981443a2fa8513fddb6d1a"

PYPI_PACKAGE = "antlr4-python3-runtime"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "nativesdk native"
