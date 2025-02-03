SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=9;endline=9;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "1345cbcdf0a75e2d89f424c559fd49fda4d8cd7be25cd5131e3b57bad8a21d76"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-pip \
    python3-pysocks \
    python3-requests \
    python3-requests-oauthlib \
    python3-six \
"
