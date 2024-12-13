SUMMARY = "Sniff out which async library your code is running under"
SECTION = "devel/python"
LICENSE = "MIT | Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=fa7b86389e58dd4087a8d2b833e5fe96 \
    file://LICENSE.MIT;md5=e62ba5042d5983462ad229f5aec1576c \
    file://LICENSE.APACHE2;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f4324edc670a0f49750a81b895f35c3adb843cca46f0530f79fc1babb23789dc"

DEPENDS += "\
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    python3-numbers \
    python3-core \
"
