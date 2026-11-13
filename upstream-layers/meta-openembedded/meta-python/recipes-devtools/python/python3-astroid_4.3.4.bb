SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a70cf540abf41acb644ac3b621b2fad1"

SRC_URI[sha256sum] = "d515a105722b72098bbe82d430d65e635f742b6cbac3bdfaf8b7c188b87c5e39"

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
