SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b9bbbb543b6af3e6b53f9b7fb68f71d"


SRC_URI[md5sum] = "d22757338542e3742a335cea6210e419"
SRC_URI[sha256sum] = "52ddbaeca2dccf53ba2d6dec5676ca7bc3b2403ef8b37f7da78b7654bb3e10f0"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-numpy python3-pillow"
