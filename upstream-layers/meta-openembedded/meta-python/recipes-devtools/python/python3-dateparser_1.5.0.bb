DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "08d62f2c00d3a1e806fc59d40c79e7554e31d84d3b8c6145820af051c92e7292"

inherit pypi python_setuptools_build_meta

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
