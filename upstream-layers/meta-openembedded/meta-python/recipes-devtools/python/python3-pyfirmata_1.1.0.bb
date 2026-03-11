SUMMARY = "A Python interface for the Firmata protocol"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84ddcef430b7c44caa22b2ff4b37a3df"

PYPI_PACKAGE = "pyFirmata"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "\
    python3-pyserial \
"

SRC_URI[sha256sum] = "cc180d1b30c85a2bbca62c15fef1b871db048cdcfa80959968356d97bd3ff08e"

inherit pypi setuptools3
