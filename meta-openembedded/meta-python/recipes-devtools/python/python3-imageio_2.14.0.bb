SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b9bbbb543b6af3e6b53f9b7fb68f71d"

SRC_URI[sha256sum] = "1a612b46c24805115701ed7c4e1f2d7feb53bb615d52bfef9713a6836e997bb1"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
