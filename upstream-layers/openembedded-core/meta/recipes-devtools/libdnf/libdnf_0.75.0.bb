SUMMARY = "Library providing simplified C and Python API to libsolv"
HOMEPAGE = "https://github.com/rpm-software-management/libdnf"
DESCRIPTION = "This library provides a high level package-manager. It's core library of dnf, PackageKit and rpm-ostree. It's replacement for deprecated hawkey library which it contains inside and uses librepo under the hood."
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/libdnf;branch=dnf-4-master;protocol=https \
           file://0004-Set-libsolv-variables-with-pkg-config-cmake-s-own-mo.patch \
           file://0001-Get-parameters-for-both-libsolv-and-libsolvext-libdn.patch \
           file://0001-drop-FindPythonInstDir.cmake.patch \
           file://armarch.patch \
           file://0001-dnf-repo-Define-FNM_EXTMATCH-if-not-already-like-und.patch \
           file://0001-utils-utils.cpp-fix-compilation-with-musl.patch \
           "

SRCREV = "d39573195e24b43687587a8d83b9f6ac274e2412"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(?!4\.90)\d+(\.\d+)+)"

DEPENDS = "glib-2.0 libsolv librepo rpm libmodulemd json-c swig-native util-linux"

inherit cmake pkgconfig setuptools3-base gettext

COMPATIBLE_HOST_libc-musl = 'null'

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3 \
                  -DWITH_GTKDOC=OFF -DWITH_MAN=OFF -DWITH_HTML=OFF \
                  -DWITH_TESTS=OFF \
                  -DWITH_ZCHUNK=OFF \
                "

BBCLASSEXTEND = "native nativesdk"

SKIP_RECIPE[libdnf] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'Does not build without package_rpm in PACKAGE_CLASSES due disabled rpm support in libsolv', d)}"
