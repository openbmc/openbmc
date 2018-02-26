SUMMARY = " A library providing C and Python (libcURL like) API for downloading linux repository metadata and packages."
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/librepo.git \
           file://0001-Correctly-set-the-library-installation-directory.patch \
           file://0002-Do-not-try-to-obtain-PYTHON_INSTALL_DIR-by-running-p.patch \
           file://0003-tests-fix-a-race-when-deleting-temporary-directories.patch \
           file://0004-Set-gpgme-variables-with-pkg-config-not-with-cmake-m.patch \
           file://0005-Fix-typo-correct-LRO_SSLVERIFYHOST-with-CURLOPT_SSL_.patch \
           "

PV = "1.7.20+git${SRCPV}"
SRCREV = "e1137cbbda78fecb192146300790680a5bc811b1"

S = "${WORKDIR}/git"

DEPENDS = "curl expat glib-2.0 openssl attr libcheck gpgme"

inherit cmake distutils3-base pkgconfig

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "native nativesdk"

