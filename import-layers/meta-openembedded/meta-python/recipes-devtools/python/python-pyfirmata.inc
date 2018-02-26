SUMMARY = "A Python interface for the Firmata protocol"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84ddcef430b7c44caa22b2ff4b37a3df"
PYPI_PACKAGE = "pyFirmata"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-pyserial \
"

SRC_URI[md5sum] = "ee4c23ca5c35d3091d550a63540811a3"
SRC_URI[sha256sum] = "b54d49d66a9d93c1ef9c72ca01264414cb2de177baac116c12d5cce0c380160c"

inherit pypi
