SUMMARY = "Library providing simplified C and Python API to libsolv"
HOMEPAGE = "https://github.com/rpm-software-management/libdnf"
DESCRIPTION = "This library provides a high level package-manager. It's core library of dnf, PackageKit and rpm-ostree. It's replacement for deprecated hawkey library which it contains inside and uses librepo under the hood."
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/libdnf;branch=dnf-4-master \
           file://0001-FindGtkDoc.cmake-drop-the-requirement-for-GTKDOC_SCA.patch \
           file://0004-Set-libsolv-variables-with-pkg-config-cmake-s-own-mo.patch \
           file://0001-Get-parameters-for-both-libsolv-and-libsolvext-libdn.patch \
           file://0001-Add-WITH_TESTS-option.patch \
           file://0001-Look-fo-sphinx-only-if-documentation-is-actually-ena.patch \
           file://enable_test_data_dir_set.patch \
           file://0001-libdnf-config.h-avoid-the-use-of-non-portable-__WORD.patch \
           file://0001-drop-FindPythonInstDir.cmake.patch \
           "

SRCREV = "8e451380bf84bd76e3925cbae2a06bb0e56f5cd9"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(?!4\.90)\d+(\.\d+)+)"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 libsolv libcheck librepo rpm gtk-doc libmodulemd json-c swig-native"

inherit gtk-doc gobject-introspection cmake pkgconfig distutils3-base

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DWITH_MAN=OFF -DPYTHON_DESIRED=3 \
                  ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DWITH_GIR=ON', '-DWITH_GIR=OFF', d)} \
                  -DWITH_TESTS=OFF \
                  -DWITH_ZCHUNK=OFF \
                  -DWITH_HTML=OFF \
                "
EXTRA_OECMAKE:append:class-native = " -DWITH_GIR=OFF"
EXTRA_OECMAKE:append:class-nativesdk = " -DWITH_GIR=OFF"

BBCLASSEXTEND = "native nativesdk"
PNBLACKLIST[libdnf] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'Does not build without package_rpm in PACKAGE_CLASSES due disabled rpm support in libsolv', d)}"

