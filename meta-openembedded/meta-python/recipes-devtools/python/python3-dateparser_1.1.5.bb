DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "107f3cc87a60770e10d111349adc1504224a6b60753a47a64b0ec842ab85b5a9"

PYPI_PACKAGE = "dateparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-dateutil \
        ${PYTHON_PN}-tzlocal \
        ${PYTHON_PN}-ruamel-yaml \
"

# Ommitted ${PYTHON_PN}-convertdate, ${PYTHON_PN}-jdatetime ${PYTHON_PN}-umalqurra
