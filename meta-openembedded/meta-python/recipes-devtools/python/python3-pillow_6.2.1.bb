SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=55c0f320370091249c1755c0d2b48e89"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=6.2.x \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
"
SRCREV ?= "6e0f07bbe38def22d36ee176b2efd9ea74b453a6"


inherit setuptools3

DEPENDS += " \
    zlib \
    jpeg \
    tiff \
    freetype \
    lcms \
    openjpeg \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
"

CVE_PRODUCT = "pillow"

S = "${WORKDIR}/git"

RPROVIDES_${PN} += "python3-imaging"

BBCLASSEXTEND = "native"
