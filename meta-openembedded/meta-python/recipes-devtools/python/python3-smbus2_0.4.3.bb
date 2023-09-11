SUMMARY = "Drop-in replacement for smbus-cffi/smbus-python in pure Python"
DESCRIPTION = "smbus2 is a drop-in replacement for smbus-cffi/smbus-python in pure Python"
HOMEPAGE = "https://github.com/kplindegaard/smbus2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools3

SRC_URI[sha256sum] = "36f2288a8e1a363cb7a7b2244ec98d880eb5a728a2494ac9c71e9de7bf6a803a"

CLEANBROKEN = "1"

PYPI_PACKAGE = "smbus2"

RDEPENDS:${PN} += "\
        ${PYTHON_PN}-ctypes \
        ${PYTHON_PN}-fcntl \
"
