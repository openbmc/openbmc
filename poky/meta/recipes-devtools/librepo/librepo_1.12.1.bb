SUMMARY = "A library providing C and Python (libcURL like) API \
           for downloading linux repository metadata and packages."
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/librepo.git \
           file://0002-Do-not-try-to-obtain-PYTHON_INSTALL_DIR-by-running-p.patch \
           file://0004-Set-gpgme-variables-with-pkg-config-not-with-cmake-m.patch \
           "

SRCREV = "d4ad350291f2937c0b6a3eea9e1d0c8e1051fc32"

S = "${WORKDIR}/git"

DEPENDS = "curl glib-2.0 openssl attr gpgme libxml2"

inherit cmake distutils3-base pkgconfig

EXTRA_OECMAKE = " \
    -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} \
    -DPYTHON_DESIRED=3 \
    -DENABLE_TESTS=OFF \
    -DENABLE_DOCS=OFF \
    -DWITH_ZCHUNK=OFF \
"

BBCLASSEXTEND = "native nativesdk"
