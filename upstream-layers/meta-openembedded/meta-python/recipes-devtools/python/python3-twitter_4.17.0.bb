SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48c84b17f84a9a623754604ab73f28fe"

SRC_URI[sha256sum] = "c232e9fffa0b15b81d5ff8ec59d46af85e330285ffc3e64614d2418fb9af71b5"

PYPI_PACKAGE = "tweepy"

inherit pypi python_flit_core

RDEPENDS:${PN} += "\
    python3-pip \
    python3-pysocks \
    python3-requests \
    python3-requests-oauthlib \
    python3-six \
"

CVE_PRODUCT = "tweepy"
CVE_STATUS[CVE-2012-5825] = "fixed-version: The vulnerability has been fixed since v3.1.0"
