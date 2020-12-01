SUMMARY = "Drop-in replacement for smbus-cffi/smbus-python in pure Python"
DESCRIPTION = "smbus2 is a drop-in replacement for smbus-cffi/smbus-python in pure Python"
HOMEPAGE = "https://github.com/kplindegaard/smbus2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit pypi setuptools3

SRC_URI[md5sum] = "d5ed5acc889b4770a84cc932853ed20a"
SRC_URI[sha256sum] = "210e66eebe4d0b1fe836b3ec2751841942e1c4918c0b429b20a0e20a222228b4"

CLEANBROKEN = "1"

PYPI_PACKAGE = "smbus2"

RDEPENDS_${PN} += "\
        ${PYTHON_PN}-ctypes \
        ${PYTHON_PN}-fcntl \
"
