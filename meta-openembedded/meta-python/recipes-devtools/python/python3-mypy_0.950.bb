SUMMARY = "Optional static typing for Python 3 and 2 (PEP 484)"
HOMEPAGE = "https://github.com/python/mypy"
LICENSE = "MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ba8ec528da02073b7e1f4124c0f836f"

PYPI_PACKAGE = "mypy"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1b333cfbca1762ff15808a0ef4f71b5d3eed8528b23ea1c3fb50543c867d68de"

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
