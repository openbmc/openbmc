SUMMARY = "Python Imaging Library (Fork). Pillow is the friendly PIL fork by Alex \
Clark and Contributors. PIL is the Python Imaging Library by Fredrik Lundh and \
Contributors."
HOMEPAGE = "https://pillow.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b22dc6b663b9175820e5e03337c7596b"

SRC_URI = "git://github.com/python-pillow/Pillow.git;branch=main;protocol=https \
           file://0001-support-cross-compiling.patch \
           file://0001-explicitly-set-compile-options.patch \
           file://run-ptest \
           "
SRCREV ?= "da59ad000d1405eaecd557175e29083a87d19f7c"

inherit setuptools3 ptest

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

RDEPENDS:${PN}-ptest += " \
    bash \
    ghostscript \
    jpeg-tools \
    libwebp \
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
