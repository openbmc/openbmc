SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "7c111da4cefd244433cb63377aff081a40f84bddae9e6f376c67f086c0f806da"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-six \
"
