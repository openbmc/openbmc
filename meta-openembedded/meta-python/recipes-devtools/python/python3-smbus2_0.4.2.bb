SUMMARY = "Drop-in replacement for smbus-cffi/smbus-python in pure Python"
DESCRIPTION = "smbus2 is a drop-in replacement for smbus-cffi/smbus-python in pure Python"
HOMEPAGE = "https://github.com/kplindegaard/smbus2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools3

SRC_URI[sha256sum] = "634541ed794068a822fe7499f1577468b9d4641b68dd9bfb6d0eb7270f4d2a32"

CLEANBROKEN = "1"

PYPI_PACKAGE = "smbus2"

RDEPENDS:${PN} += "\
        ${PYTHON_PN}-ctypes \
        ${PYTHON_PN}-fcntl \
"
