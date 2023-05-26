SUMMARY = "ANTLR runtime for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=b38bac4871765ce562806c44d2f18cf1"

SRC_URI[sha256sum] = "0d5454928ae40c8a6b653caa35046cd8492c8743b5fbc22ff4009099d074c7ae"

PYPI_PACKAGE = "antlr4-python3-runtime"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "nativesdk native"
