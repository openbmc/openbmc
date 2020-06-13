SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

GNOMEBASEBUILDCLASS = "meson"

GIR_MESON_OPTION = "enable-gir"

inherit setuptools3 gnomebase gobject-introspection vala

DEPENDS += "lcms"

# https://bugs.llvm.org/show_bug.cgi?id=45555
CFLAGS_append_toolchain-clang_mipsarch = " -ffp-exception-behavior=ignore "
CFLAGS_append_toolchain-clang_riscv64 = " -ffp-exception-behavior=ignore "

SRC_URI = "https://download.gimp.org/pub/${BPN}/0.1/${BP}.tar.xz"
SRC_URI[md5sum] = "b1a85d1f3d710407164848708f1f49f1"
SRC_URI[sha256sum] = "17d5493633bff5585d9f375bc4df5925157cd1c70ccd7c22a635be75c172523a"

BBCLASSEXTEND = "native"
