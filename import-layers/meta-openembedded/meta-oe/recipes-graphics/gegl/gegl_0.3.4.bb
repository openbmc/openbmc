SUMMARY = "GEGL (Generic Graphics Library) is a graph based image processing framework"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "babl librsvg glib-2.0 gtk+ pango cairo expat zlib libpng jpeg virtual/libsdl json-glib"

EXTRA_OECONF = "--disable-docs"

inherit gnomebase vala gobject-introspection

PACKAGECONFIG ??= ""
PACKAGECONFIG[jasper] = "--with-jasper,--without-jasper,jasper"
PACKAGECONFIG[avformat] = "--with-libavformat,--without-libavformat,libav"
PACKAGECONFIG[lcms] = "--with-lcms,--without-lcms,lcms"
PACKAGECONFIG[tiff] = "--with-libtiff,--without-libtiff,tiff"
PACKAGECONFIG[webp] = "--with-webp,--without-webp,webp"

SRC_URI = "http://ftp.gimp.org/pub/${BPN}/0.3/${BP}.tar.bz2 \
           file://pkgconfig.patch "
SRC_URI[md5sum] = "c19478321594d715a4cb324a0decda6f"
SRC_URI[sha256sum] = "846290a790854d1e6b7c17a2d6f82ad7cb14c72e240bd3b81b98cc0ceddbc3ec"

# There are a couple of non-symlink .so files installed into libdir, which need to go into main package
FILES_${PN}_append = " ${libdir}/gegl-0.3/*.so ${libdir}/gegl-0.3/*.json ${libdir}/libgegl-npd-0.3.so ${libdir}/libgegl-sc-0.3.so"
FILES_${PN}-dev_append = " ${libdir}/gegl-0.3/*.la ${libdir}/libgegl-0.3.so"
FILES_${PN}-dev_remove = "${libdir}/lib*.so"

# Fails to build with thumb-1 (qemuarm)
# gegl-0.2.0/operations/common/matting-global.c: In function 'matting_process':
# gegl-0.2.0/operations/common/matting-global.c:463:1: internal compiler error: in patch_jump_insn, at cfgrtl.c:1275
ARM_INSTRUCTION_SET = "arm"
