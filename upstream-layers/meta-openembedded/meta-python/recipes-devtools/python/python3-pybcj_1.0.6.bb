SUMMARY = "bcj filter library"
HOMEPAGE = "https://codeberg.org/miurahr/pybcj"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[sha256sum] = "70bbe2dc185993351955bfe8f61395038f96f5de92bb3a436acb01505781f8f2"

inherit pypi python_setuptools_build_meta pypi

#PROVIDES = "python3-pybcj"

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-email \
    python3-importlib-metadata \
    python3-core \
    python3-compression \
"
