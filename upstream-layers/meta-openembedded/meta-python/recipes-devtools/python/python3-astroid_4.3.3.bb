SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a70cf540abf41acb644ac3b621b2fad1"

SRC_URI[sha256sum] = "d03854b09d92c08e18d8e7d9185d393961186ed0747136d8fcb2d1c008a504ec"

inherit pypi python_setuptools_build_meta

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/test* \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/__pycache__/test* \
"

RDEPENDS:${PN}:append:class-target = " \
    python3-lazy-object-proxy \
    python3-logging \
    python3-wrapt \
    python3-setuptools \
    python3-typing-extensions \
"

RDEPENDS:${PN}-tests:append:class-target = " \
    python3-unittest \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
