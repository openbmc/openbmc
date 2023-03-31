SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "169f1642cdb723133fe8fe901887f4f1b39bc036458c4664f1f9d256226ced35"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
