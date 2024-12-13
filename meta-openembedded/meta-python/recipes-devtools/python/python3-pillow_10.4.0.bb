DESCRIPTION = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "HPND"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c349a4b4b9ec2377a8fd6a7df87dbffe"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=main;protocol=https \
           file://0001-support-cross-compiling.patch \
           file://run-ptest \
           "
SRCREV = "9b4fae77178e827ab17118fbc89c739ffd6a0fab"

inherit python_setuptools_build_meta ptest

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
    python3-pytest \
    python3-pytest-timeout \
    python3-resource \
    python3-unittest-automake-output \
    python3-unixadmin\
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'tk', '', d)} \
"

CVE_PRODUCT = "pillow"

S = "${WORKDIR}/git"

RPROVIDES:${PN} += "python3-imaging"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/Tests
    cp -rf ${S}/Tests ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native"
