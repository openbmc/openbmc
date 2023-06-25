SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "f8436a02af02fd63f272dab50f7d623547a38f0e04a4a73e2b02ae1b8b180f27"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
