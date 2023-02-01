SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "b80796a1f8c38c697a940a2ad7397ee28900d5c4e51061b9a67d16aca867f33e"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
