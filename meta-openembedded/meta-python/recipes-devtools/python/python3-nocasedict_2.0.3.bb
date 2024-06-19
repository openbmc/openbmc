SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "1c9226c7f5a8a97ad51dcb0ae3157a720e3f7cb9c4568d22ea3a05e3f85658a9"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-six \
"
