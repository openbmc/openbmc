DESCRIPTION = "Library to implement a well-behaved Unix daemon process"
HOMEPAGE = "https://pagure.io/python-daemon/"
SECTION = "devel/python"

DEPENDS += "${PYTHON_PN}-docutils-native"
RDEPENDS:${PN} = "${PYTHON_PN}-docutils \
                  ${PYTHON_PN}-lockfile (>= 0.10) \
                  ${PYTHON_PN}-resource \
"

LICENSE = "Apache-2.0 & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://README;md5=a3a94c615dc969a70525f1eebbacf235"

inherit pypi setuptools3

SRC_URI[md5sum] = "b7397fe73d516dc14921500a1245b41c"
SRC_URI[sha256sum] = "3deeb808e72b6b89f98611889e11cc33754f5b2c1517ecfa1aaf25f402051fb5"

PYPI_PACKAGE = "python-daemon"
