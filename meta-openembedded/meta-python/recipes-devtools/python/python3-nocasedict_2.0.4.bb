SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "4ca934f65df57b10d0fcab5f0c39e9dccb93577ff9f22bef98265ddbf12f8af1"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-six \
"
