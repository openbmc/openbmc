DESCRIPTION = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT-CMU"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a1b708da743e3fc0e5c35e92daac0bf8"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=main;protocol=https;tag=${PV} \
           file://0001-support-cross-compiling.patch \
           "
SRCREV = "89f1f4626a2aaf5f3d5ca6437f41def2998fbe09"

inherit python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "Tests"

PEP517_BUILD_OPTS += " \
    -C platform-guessing=disable \
    -C zlib=enable \
    -C jpeg=enable \
    -C tiff=enable \
    -C freetype=enable \
    -C lcms=enable \
    -C jpeg2000=enable \
    -C webp=disable \
    -C webpmux=disable \
    -C imagequant=disable \
"

DEPENDS += " \
    zlib \
    jpeg \
    tiff \
    freetype \
    lcms \
    openjpeg \
"

RDEPENDS:${PN} += " \
    python3-misc \
    python3-logging \
    python3-numbers \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxcb', '', d)} \
"
# python3-compile for filecmp module
RDEPENDS:${PN}-ptest += " \
    bash \
    ghostscript \
    jpeg-tools \
    libwebp \
    python3-compile \
    python3-core \
    python3-image \
    python3-mmap \
    python3-pytest-timeout \
    python3-resource \
    python3-unixadmin\
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'tk', '', d)} \
"

CVE_PRODUCT = "pillow"


RPROVIDES:${PN} += "python3-imaging"

BBCLASSEXTEND = "native"
