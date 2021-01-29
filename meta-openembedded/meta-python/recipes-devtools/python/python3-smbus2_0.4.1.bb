SUMMARY = "Drop-in replacement for smbus-cffi/smbus-python in pure Python"
DESCRIPTION = "smbus2 is a drop-in replacement for smbus-cffi/smbus-python in pure Python"
HOMEPAGE = "https://github.com/kplindegaard/smbus2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6276eb599b76c4e74372f2582d2282f03b4398f0da16bc996608e4f21557ca9b"

CLEANBROKEN = "1"

PYPI_PACKAGE = "smbus2"

RDEPENDS_${PN} += "\
        ${PYTHON_PN}-ctypes \
        ${PYTHON_PN}-fcntl \
"
