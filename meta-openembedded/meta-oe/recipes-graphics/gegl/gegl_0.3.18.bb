SUMMARY = "GEGL (Generic Graphics Library) is a graph based image processing framework"
LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "babl librsvg glib-2.0 gtk+ pango cairo expat zlib libpng jpeg virtual/libsdl json-glib intltool-native"

EXTRA_OECONF = "--disable-docs"

inherit distro_features_check gnomebase vala gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= ""
PACKAGECONFIG[jasper] = "--with-jasper,--without-jasper,jasper"
PACKAGECONFIG[avformat] = "--with-libavformat,--without-libavformat,libav"
PACKAGECONFIG[lcms] = "--with-lcms,--without-lcms,lcms"
PACKAGECONFIG[tiff] = "--with-libtiff,--without-libtiff,tiff"
PACKAGECONFIG[webp] = "--with-webp,--without-webp,webp"

SRC_URI = "http://ftp.gimp.org/pub/${BPN}/0.3/${BP}.tar.bz2 \
           file://pkgconfig.patch "
SRC_URI[md5sum] = "6e5c6f229261478dc436a38c84405b2a"
SRC_URI[sha256sum] = "d7858ef26ede136d14e3de188a9e9c0de7707061a9fb96d7d615fab4958491fb"

LDFLAGS += "-lm"

# There are a couple of non-symlink .so files installed into libdir, which need to go into main package
FILES_${PN}_append = " ${libdir}/gegl-0.3/*.so ${libdir}/gegl-0.3/*.json ${libdir}/libgegl-npd-0.3.so ${libdir}/libgegl-sc-0.3.so"
FILES_${PN}-dev_append = " ${libdir}/gegl-0.3/*.la ${libdir}/libgegl-0.3.so"
FILES_${PN}-dev_remove = "${libdir}/lib*.so"

# Fails to build with thumb-1 (qemuarm)
# gegl-0.2.0/operations/common/matting-global.c: In function 'matting_process':
# gegl-0.2.0/operations/common/matting-global.c:463:1: internal compiler error: in patch_jump_insn, at cfgrtl.c:1275
ARM_INSTRUCTION_SET = "arm"
