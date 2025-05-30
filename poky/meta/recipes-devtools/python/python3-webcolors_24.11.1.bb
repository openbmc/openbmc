SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
HOMEPAGE = "https://pypi.org/project/webcolors/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbaebec43b7d199c7fd8f5411b3b0448"

SRC_URI[sha256sum] = "ecb3d768f32202af770477b8b65f318fa4f566c22948673a977b00d589dd80f6"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += " \
    python3-pdm-native \
    python3-pdm-backend-native \
"

RDEPENDS:${PN}:class-target = "\
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
