SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a70cf540abf41acb644ac3b621b2fad1"

SRC_URI[sha256sum] = "a88c7994f914a4ea8572fac479459f4955eeccc877be3f2d959a33273b0cf40b"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-pytest-runner-native \
    python3-wheel-native \
"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/test* \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/__pycache__/test* \
"

RDEPENDS:${PN}:class-target += "\
    python3-lazy-object-proxy \
    python3-logging \
    python3-six \
    python3-wrapt \
    python3-setuptools \
    python3-typing-extensions \
"

RDEPENDS:${PN}-tests:class-target += "\
    python3-unittest \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
