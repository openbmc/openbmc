SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b9bbbb543b6af3e6b53f9b7fb68f71d"

SRC_URI[sha256sum] = "7f7d8d8e1eb6f8bb1d15e0dd93bee3f72026a4c3b96e9c690e42f403f7bdea3e"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
