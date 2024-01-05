SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b22dc6b663b9175820e5e03337c7596b"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=10.0.x;protocol=https \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
           file://run-ptest \
           "
SRCREV ?= "da59ad000d1405eaecd557175e29083a87d19f7c"

inherit setuptools3 ptest

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
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxcb', '', d)} \
"

RDEPENDS:${PN}-ptest += " \
    bash \
    ghostscript \
    jpeg-tools \
    libwebp \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-image \
    ${PYTHON_PN}-mmap \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-pytest-timeout \
    ${PYTHON_PN}-resource \
    ${PYTHON_PN}-unixadmin\
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

SRCREV = "e34d346f10c0b1c814661e662a3e0c1ef084cf1c"
