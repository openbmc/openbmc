SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "ebe159ca847304949120650aabef6698ff26c4528c55760487759283632ce7e8"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
"

RDEPENDS:${PN} += " \
    python3-six \
"
