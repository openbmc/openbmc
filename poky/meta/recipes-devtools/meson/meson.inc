HOMEPAGE = "http://mesonbuild.com"
SUMMARY = "A high performance build system"
DESCRIPTION = "Meson is a build system designed to increase programmer \
productivity. It does this by providing a fast, simple and easy to use \
interface for modern software development tools and practices."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "https://github.com/mesonbuild/meson/releases/download/${PV}/meson-${PV}.tar.gz \
           file://0001-gtkdoc-fix-issues-that-arise-when-cross-compiling.patch \
           file://0002-gobject-introspection-determine-g-ir-scanner-and-g-i.patch \
           file://0003-native_bindir.patch \
           file://0001-python-module-do-not-manipulate-the-environment-when.patch \
           file://disable-rpath-handling.patch \
           file://cross-prop-default.patch \
           file://0001-mesonbuild-environment.py-check-environment-for-vari.patch \
           file://0001-modules-python.py-do-not-substitute-python-s-install.patch \
           file://dbc9e971bd320f3df15c1ee74f54858e6792b183.patch \
           "
SRC_URI[sha256sum] = "d60f75f0dedcc4fd249dbc7519d6f3ce6df490033d276ef1cf27453ef4938d32"
SRC_URI[md5sum] = "7ea7772414dda8ae11072244bf7ba991"

SRC_URI_append_class-native = " \
    file://0001-Make-CPU-family-warnings-fatal.patch \
    file://0002-Support-building-allarch-recipes-again.patch \
"

UPSTREAM_CHECK_URI = "https://github.com/mesonbuild/meson/releases"

inherit setuptools3

RDEPENDS_${PN} = "ninja python3-modules python3-pkg-resources"

FILES_${PN} += "${datadir}/polkit-1"
