DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "faa2b97f51f3b5ff1ba2f17be90de2b733fb6191f89b4058787473e8202f3044"

PYPI_PACKAGE = "dateparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-dateutil \
        ${PYTHON_PN}-tzlocal \
        ${PYTHON_PN}-ruamel-yaml \
"

# Ommitted ${PYTHON_PN}-convertdate, ${PYTHON_PN}-jdatetime ${PYTHON_PN}-umalqurra
