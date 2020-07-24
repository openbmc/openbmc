DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[md5sum] = "78b4bf317f9b844631562abad5ce040b"
SRC_URI[sha256sum] = "e875efd8c57c85c2d02b238239878db59ff1971f5a823457fcc69e493bf6ebfa"

PYPI_PACKAGE = "dateparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-dateutil \
        ${PYTHON_PN}-tzlocal \
        ${PYTHON_PN}-ruamel-yaml \
"

# Ommitted ${PYTHON_PN}-convertdate, ${PYTHON_PN}-jdatetime ${PYTHON_PN}-umalqurra
