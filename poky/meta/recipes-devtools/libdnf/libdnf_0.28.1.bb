SUMMARY = "Library providing simplified C and Python API to libsolv"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/rpm-software-management/libdnf \
           file://0001-FindGtkDoc.cmake-drop-the-requirement-for-GTKDOC_SCA.patch \
           file://0004-Set-libsolv-variables-with-pkg-config-cmake-s-own-mo.patch \
           file://0001-Get-parameters-for-both-libsolv-and-libsolvext-libdn.patch \
           file://0001-Add-WITH_TESTS-option.patch \
           file://0001-include-stdexcept-for-runtime_error.patch \
           file://fix-deprecation-warning.patch \
           "

SRCREV = "751f89045b80d58c0d05800f74357cf78cdf7e77"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 libsolv libcheck librepo rpm gtk-doc libmodulemd-v1 json-c swig-native"

inherit gtk-doc gobject-introspection cmake pkgconfig distutils3-base

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DWITH_MAN=OFF -DPYTHON_DESIRED=3 \
                  ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DWITH_GIR=ON', '-DWITH_GIR=OFF', d)} \
                  -DWITH_TESTS=OFF \
                "
EXTRA_OECMAKE_append_class-native = " -DWITH_GIR=OFF"
EXTRA_OECMAKE_append_class-nativesdk = " -DWITH_GIR=OFF"

BBCLASSEXTEND = "native nativesdk"
PNBLACKLIST[libdnf] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'Does not build without package_rpm in PACKAGE_CLASSES due disabled rpm support in libsolv', d)}"

