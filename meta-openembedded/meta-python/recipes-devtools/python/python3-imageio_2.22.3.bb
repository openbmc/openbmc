SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "63f007b7f2a082306e36922b3fd529a7aa305d2b78f46195bab8e22bbfe866e9"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
