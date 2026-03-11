SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "b563d5851cbb0e0b10fbb6189ba87e0612d22e5a6f3a004a4573ab5b38aaaa7d"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
"

RDEPENDS:${PN} += " \
    python3-six \
"
