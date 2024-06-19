SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "f13eb76e4922f936ac4a7fec77ce8a783e63b93543d4ea3e40793a6cabd9ac7d"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
