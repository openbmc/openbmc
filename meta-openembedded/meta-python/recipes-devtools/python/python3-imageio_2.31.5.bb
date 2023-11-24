SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "d8e53f9cd4054880276a3dac0a28c85ba7874084856a55a0294a8ae6ed7f3a8e"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
