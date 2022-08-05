SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "eb3cd70de8be87b72ea85716b7363c700b91144589ee6b5d7b49d42998b7d185"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
