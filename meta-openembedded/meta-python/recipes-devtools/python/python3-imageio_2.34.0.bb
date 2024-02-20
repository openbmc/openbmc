SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24cb9a367a9e641b459a01c4d15256ba"

SRC_URI[sha256sum] = "ae9732e10acf807a22c389aef193f42215718e16bd06eed0c5bb57e1034a4d53"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-numpy ${PYTHON_PN}-pillow"
