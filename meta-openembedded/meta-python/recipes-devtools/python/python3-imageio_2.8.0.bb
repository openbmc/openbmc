SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b9bbbb543b6af3e6b53f9b7fb68f71d"


SRC_URI[md5sum] = "1e270dbf24c0390c2f4e3e4120904ac0"
SRC_URI[sha256sum] = "fb5fd6d3d17126bbaac9af29fe340e2c97a196eb9416d4f28c0e543744a152cf"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-numpy python3-pillow"
