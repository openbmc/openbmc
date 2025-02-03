SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "71b57b3669666272c818497aebba2b4c5f20d5b37c81720e5e1a56d59c492996"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
