SUMMARY = "Universal encoding detector for Python 2 and 3"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6f89e2100d9b6cdffcea4f398e37343"

inherit pypi

# setup.py of chardet needs this.
DEPENDS += "${PYTHON_PN}-pytest-runner-native"

SRC_URI[md5sum] = "7dd1ba7f9c77e32351b0a0cfacf4055c"
SRC_URI[sha256sum] = "84ab92ed1c4d4f16916e05906b6b75a6c0fb5db821cc65e70cbd64a3e2a5eaae"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-cli"
RDEPENDS_${PN}-cli = "${PN} "
FILES_${PN}-cli += " \
    ${PYTHON_SITEPACKAGES_DIR}/chardet/cli \
"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-logging \
"
