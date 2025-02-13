DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "7e4919aeb48481dbfc01ac9683c8e20bfe95bb715a38c1e9f6af889f4f30ccc3"

PYPI_PACKAGE = "dateparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3-dateutil \
    python3-logging \
    python3-pytz \
    python3-regex \
    python3-ruamel-yaml \
    python3-tzlocal \
"

# Ommitted python3-convertdate, python3-jdatetime python3-umalqurra
