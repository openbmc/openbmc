DESCRIPTION = "Incremental is a small library that versions your Python projects"
HOMEPAGE = "https://github.com/twisted/incremental"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ca9b07f08e2c72d48c74d363d1e0e15"

SRC_URI[sha256sum] = "02f5de5aff48f6b9f665d99d48bfc7ec03b6e3943210de7cfc88856d755d6f57"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-twisted \
    ${PYTHON_PN}-click \
"

# -native is needed to build python[3]-twisted, however, we need to take steps to
# prevent a circular dependency. The build apparently does not use the part of
# python-incremental which uses python-twisted, so this hack is OK.
RDEPENDS:${PYTHON_PN}-incremental-native:remove = "${PYTHON_PN}-twisted-native"
BBCLASSEXTEND = "native"
