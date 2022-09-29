SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "5f0278217c1cf99d90ef855dab948f93d9fce0ab7ab388e13a597c706b7ec4e5"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
