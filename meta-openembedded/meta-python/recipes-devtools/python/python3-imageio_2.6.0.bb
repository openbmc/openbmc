SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8b7fdd0dff0fd18f35c05365d3d7bf7"

SRC_URI = "git://github.com/imageio/imageio.git;protocol=https;branch=master"
SRCREV = "0b161649b3ee108f80bd99466aeab2e65cf82cd8"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} = "python3-numpy python3-pillow"
