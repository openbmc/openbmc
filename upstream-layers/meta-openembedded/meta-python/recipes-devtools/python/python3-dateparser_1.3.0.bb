DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "5bccf5d1ec6785e5be71cc7ec80f014575a09b4923e762f850e57443bddbf1a5"

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
