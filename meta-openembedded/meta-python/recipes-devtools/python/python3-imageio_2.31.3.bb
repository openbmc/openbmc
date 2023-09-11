SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "74c6a832d81b7ad5a8a80976dea58ee033d3e2b99a54990cbd789b4cb0b31461"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
