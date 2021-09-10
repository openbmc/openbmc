SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0337b116233da4616ae9fdb130bf6f1a"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=8.3.x \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
"
SRCREV ?= "92933b86574b9c80764bf52c357ed29e1ef53382"

inherit setuptools3

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
