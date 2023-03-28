SUMMARY = "ANTLR runtime for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=ab3c64dc056e158882a9a6b35a2f4a6e"

SRC_URI[sha256sum] = "0a8b82f55032734f43ed6b60b8a48c25754721a75cd714eb1fe9ce6ed418b361"

PYPI_PACKAGE = "antlr4-python3-runtime"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "nativesdk native"
