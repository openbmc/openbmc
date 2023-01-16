SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bc416d18f294943285560364be7cbec1"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=main;protocol=https \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
           "
SRCREV ?= "82541b6dec8452cb612067fcebba1c5a1a2bfdc8"

inherit setuptools3

PIP_INSTALL_PACKAGE = "Pillow"
PIP_INSTALL_DIST_PATH = "${S}/dist"

DEPENDS += " \
    zlib \
    jpeg \
    tiff \
    freetype \
    lcms \
    openjpeg \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
"

CVE_PRODUCT = "pillow"

S = "${WORKDIR}/git"

RPROVIDES:${PN} += "python3-imaging"

BBCLASSEXTEND = "native"

SRCREV = "a5bbab1c1e63b439de191ef2040173713b26d2da"
