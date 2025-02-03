SUMMARY = "PPMd compression/decompression library"
HOMEPAGE = "https://pyppmd.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f1a812f1e7628f4c26d05de340b91b72165d7b62778c27d322b82ce2e8ff00cb"

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-email \
    python3-importlib-metadata \
"
