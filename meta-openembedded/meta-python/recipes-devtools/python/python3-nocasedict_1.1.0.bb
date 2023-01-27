SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "ac551de692be6aea5b43ac3f2c33780df940013ac6dd0718fb552c8b560ba661"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-six \
"
