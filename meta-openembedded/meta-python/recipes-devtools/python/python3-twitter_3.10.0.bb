SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "76e6954b806ca470dda877f57db8792fff06a0beba0ed43efc3805771e39f06a"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
