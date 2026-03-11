DESCRIPTION = "Python interface for libheif library"
HOMEPAGE = "https://github.com/bigcat88/pillow_heif"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b6c07a92aed67c33bc346748d7c7e991"

# While this item does not require it, it depends on libheif which does
LICENSE_FLAGS = "commercial"

PYPI_PACKAGE = "pillow_heif"

inherit pypi python_setuptools_build_meta

SRC_URI += "file://0001-setup.py-support-cross-compiling.patch"
SRC_URI[sha256sum] = "61d473929340d3073722f6316b7fbbdb11132faa6bac0242328e8436cc55b39a"

DEPENDS += "libheif"

RDEPENDS:${PN} += "python3-pillow"

BBCLASSEXTEND = "native nativesdk"
