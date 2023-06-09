SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "7fc6ad5b5677cb1e58077875a72512aa8c392b6d40885eca0a6ab250efb4b8f4"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
