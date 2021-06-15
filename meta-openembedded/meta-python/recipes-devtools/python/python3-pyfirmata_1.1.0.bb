SUMMARY = "A Python interface for the Firmata protocol"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84ddcef430b7c44caa22b2ff4b37a3df"
PYPI_PACKAGE = "pyFirmata"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-pyserial \
"

SRC_URI[md5sum] = "159673cfb56c72ceafc30fe91eedd847"
SRC_URI[sha256sum] = "cc180d1b30c85a2bbca62c15fef1b871db048cdcfa80959968356d97bd3ff08e"

inherit pypi setuptools3
