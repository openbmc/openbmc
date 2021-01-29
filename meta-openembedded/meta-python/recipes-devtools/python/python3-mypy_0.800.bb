SUMMARY = "Optional static typing for Python 3 and 2 (PEP 484)"
HOMEPAGE = "https://github.com/python/mypy"
LICENSE = "MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6afb7c151c4dedb5c3dc292cc120fadc"

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-mypy-extensions \
    ${PYTHON_PN}-typed-ast \
    ${PYTHON_PN}-typing-extensions \
"

PYPI_PACKAGE = "mypy"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e0202e37756ed09daf4b0ba64ad2c245d357659e014c3f51d8cd0681ba66940a"

BBCLASSEXTEND = "native"
