SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "960cb699f1209da80ac39e3ab50aa7342fe8ca9f70606c23447a510550435e50"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-six \
"
