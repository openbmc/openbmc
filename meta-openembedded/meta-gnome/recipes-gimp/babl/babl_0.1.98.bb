SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

GNOMEBASEBUILDCLASS = "meson"

GIR_MESON_OPTION = "enable-gir"

inherit setuptools3 gnomebase gobject-introspection vala

DEPENDS += "lcms"

# https://bugs.llvm.org/show_bug.cgi?id=45555
CFLAGS:append:toolchain-clang:mipsarch = " -ffp-exception-behavior=ignore "
CFLAGS:append:toolchain-clang:riscv64 = " -ffp-exception-behavior=ignore "

SRC_URI = "https://download.gimp.org/pub/${BPN}/0.1/${BP}.tar.xz"
SRC_URI[sha256sum] = "f3b222f84e462735de63fa9c3651942f2b78fd314c73a22e05ff7c73afd23af1"

BBCLASSEXTEND = "native"
