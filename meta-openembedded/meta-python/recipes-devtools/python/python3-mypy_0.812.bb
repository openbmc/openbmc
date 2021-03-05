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

SRC_URI[sha256sum] = "cd07039aa5df222037005b08fbbfd69b3ab0b0bd7a07d7906de75ae52c4e3119"

BBCLASSEXTEND = "native"
