SUMMARY = "ANTLR runtime for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=6e514123aedb5d9fb133d1bc6c598d46"

SRC_URI[sha256sum] = "061a49bc72ae05a35d9b61c0ba0ac36c0397708819f02fbfb20a80e47d287a1b"

PYPI_PACKAGE = "antlr4-python3-runtime"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "nativesdk native"
