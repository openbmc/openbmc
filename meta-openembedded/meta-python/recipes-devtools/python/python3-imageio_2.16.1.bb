SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b9bbbb543b6af3e6b53f9b7fb68f71d"

SRC_URI[sha256sum] = "7f123cb23a77ac5abe8ed4e7ad6a60831a82de2c5d123463dcf1d4278c4779d2"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
