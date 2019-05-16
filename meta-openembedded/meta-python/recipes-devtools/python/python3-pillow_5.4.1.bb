SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6379001ecb47e2a0420c40177fc1125"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=5.4.x \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
"
SRCREV ?= "f38f01bbe3a0a9f49ce592c86ff20c01c9655133"


inherit setuptools3

DEPENDS += " \
    zlib \
    jpeg \
    tiff \
    freetype \
    lcms \
    openjpeg \
"

CVE_PRODUCT = "pillow"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
