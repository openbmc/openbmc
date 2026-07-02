DESCRIPTION = "Provides modules to easily parse localized dates in almost any string formats commonly found on web pages"
HOMEPAGE = "https://github.com/scrapinghub/dateparser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d3ed25571191e7aa3f55d0a6efe0051"

SRC_URI[sha256sum] = "f265df13c0380e2e07543ba74b67c0681aaa1096981ffcd35227e1aa0cb81c7c"

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
