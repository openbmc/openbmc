SUMMARY = "A library providing C and Python (libcURL like) API \
           for downloading linux repository metadata and packages."
HOMEPAGE = "https://github.com/rpm-software-management/librepo"
DESCRIPTION = "${SUMMARY}"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/librepo.git;branch=master;protocol=https \
           file://0002-Do-not-try-to-obtain-PYTHON_INSTALL_DIR-by-running-p.patch \
           file://0004-Set-gpgme-variables-with-pkg-config-not-with-cmake-m.patch \
           file://0001-gpg_gpgme.c-fix-build-errors-with-older-gcc.patch \
           "

SRCREV = "ae727d99086f70f39fba5695af5460e1da908c1b"

S = "${WORKDIR}/git"

DEPENDS = "curl glib-2.0 openssl attr gpgme libxml2"

inherit cmake setuptools3-base pkgconfig

EXTRA_OECMAKE = " \
    -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} \
    -DPYTHON_DESIRED=3 \
    -DENABLE_TESTS=OFF \
    -DENABLE_DOCS=OFF \
    -DWITH_ZCHUNK=OFF \
    -DENABLE_SELINUX=OFF \
"

BBCLASSEXTEND = "native nativesdk"
