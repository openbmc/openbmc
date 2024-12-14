SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "e4e1d231f47f9a9e16100b0f7ce1a86e8856fb4d1c0fa2c4365a316f1746be62"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
