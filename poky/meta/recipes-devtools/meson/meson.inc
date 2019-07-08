HOMEPAGE = "http://mesonbuild.com"
SUMMARY = "A high performance build system"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "https://github.com/mesonbuild/meson/releases/download/${PV}/meson-${PV}.tar.gz \
           file://0001-gtkdoc-fix-issues-that-arise-when-cross-compiling.patch \
           file://0002-gobject-introspection-determine-g-ir-scanner-and-g-i.patch \
           file://0001-Linker-rules-move-cross_args-in-front-of-output_args.patch \
           file://0003-native_bindir.patch \
           file://0001-python-module-do-not-manipulate-the-environment-when.patch \
           file://disable-rpath-handling.patch \
           file://0001-modules-windows-split-WINDRES-env-variable.patch \
           file://0002-environment.py-detect-windows-also-if-the-system-str.patch \
           file://cross-prop-default.patch \
           file://many-cross.patch \
           file://cross-libdir.patch \
           "
SRC_URI[sha256sum] = "ef9f14326ec1e30d3ba1a26df0f92826ede5a79255ad723af78a2691c37109fd"
SRC_URI[md5sum] = "0267b0871266056184c484792572c682"

SRC_URI_append_class-native = " \
    file://0001-Make-CPU-family-warnings-fatal.patch \
    file://0002-Support-building-allarch-recipes-again.patch \
"

UPSTREAM_CHECK_URI = "https://github.com/mesonbuild/meson/releases"

inherit setuptools3

RDEPENDS_${PN} = "ninja python3-core python3-modules"

FILES_${PN} += "${datadir}/polkit-1"
