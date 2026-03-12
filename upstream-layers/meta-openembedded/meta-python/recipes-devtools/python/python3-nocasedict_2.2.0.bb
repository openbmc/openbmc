SUMMARY = "A case-insensitive ordered dictionary for Python"
HOMEPAGE = "https://github.com/pywbem/nocasedict"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI[sha256sum] = "6f2b608602b0568cd06ff46e2a0f231d2a0c247d6dc120672c381cb29169e1e7"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
"

RDEPENDS:${PN} += " \
    python3-six \
"
