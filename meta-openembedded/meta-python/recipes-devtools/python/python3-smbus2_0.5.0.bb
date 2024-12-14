SUMMARY = "Drop-in replacement for smbus-cffi/smbus-python in pure Python"
DESCRIPTION = "smbus2 is a drop-in replacement for smbus-cffi/smbus-python in pure Python"
HOMEPAGE = "https://github.com/kplindegaard/smbus2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2a3eca2de44816126b3c6f33811a9fba"

inherit pypi setuptools3

SRC_URI[sha256sum] = "4a5946fd82277870c2878befdb1a29bb28d15cda14ea4d8d2d54cf3d4bdcb035"

CLEANBROKEN = "1"

PYPI_PACKAGE = "smbus2"

RDEPENDS:${PN} += "\
        python3-ctypes \
        python3-fcntl \
"
