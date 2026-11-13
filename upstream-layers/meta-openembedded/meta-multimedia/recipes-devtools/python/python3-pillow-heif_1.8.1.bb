DESCRIPTION = "Python interface for libheif library"
HOMEPAGE = "https://github.com/bigcat88/pillow_heif"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b6c07a92aed67c33bc346748d7c7e991"

# While this item does not require it, it depends on libheif which does
LICENSE_FLAGS = "commercial"

inherit pypi python_setuptools_build_meta

SRC_URI += "file://0001-setup.py-support-cross-compiling.patch"
SRC_URI[sha256sum] = "521ebffb8a181d56c3904e5a61f20903edee0d9d3275967b8fb345f866215c06"

DEPENDS += "libheif python3-wheel-native"

RDEPENDS:${PN} += "python3-pillow"
