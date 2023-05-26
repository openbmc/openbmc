SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "5aa207ab61aca233a7a312951ac603d618b78418eac749e3dc5035010531e25b"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-numpy python3-pillow"
