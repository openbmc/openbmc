SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "78722d40b137bd98f5ec7312119f8aea9ad2049f76f434748eb306b6937cc1ce"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
