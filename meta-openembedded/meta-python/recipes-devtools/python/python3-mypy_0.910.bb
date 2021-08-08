SUMMARY = "Optional static typing for Python 3 and 2 (PEP 484)"
HOMEPAGE = "https://github.com/python/mypy"
LICENSE = "MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=17b7180fcfc43c4e70c07c71588604c4"

PYPI_PACKAGE = "mypy"

inherit pypi setuptools3

SRC_URI[sha256sum] = "704098302473cb31a218f1775a873b376b30b4c18229421e9e9dc8916fd16150"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-mypy-extensions \
    ${PYTHON_PN}-typed-ast \
    ${PYTHON_PN}-typing-extensions \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-difflib \
    ${PYTHON_PN}-toml \
"
