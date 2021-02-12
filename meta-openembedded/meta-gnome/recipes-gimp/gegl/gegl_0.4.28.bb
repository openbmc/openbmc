SUMMARY = "GEGL (Generic Graphics Library) is a graph based image processing framework"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1a8bfcbc85304df454b65d378b299c7"

DEPENDS = " \
    intltool-native \
    babl \
    glib-2.0 \
    pango \
    cairo \
    expat \
    zlib \
    \
    json-glib \
"

GNOMEBASEBUILDCLASS = "meson"

inherit features_check gnomebase vala gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SHPV = "${@gnome_verdir("${PV}")}"

SRC_URI = " \
    https://download.gimp.org/pub/${BPN}/${SHPV}/${BP}.tar.xz \
    file://0001-meson.build-Give-note-if-sdl2-was-found.patch \
"
SRC_URI[sha256sum] = "1d110d8577d54cca3b34239315bd37c57ccb27dd4355655074a2d2b3fd897900"

PACKAGECONFIG ??= "gexiv2 jpeg libpng librsvg sdl2"
PACKAGECONFIG_class-native = "libpng librsvg"

PACKAGECONFIG[jasper] = "-Djasper=enabled,-Djasper=disabled,jasper"
PACKAGECONFIG[gexiv2] = "-Dgexiv2=enabled,-Dgexiv2=disabled,gexiv2"
PACKAGECONFIG[graphviz] = "-Dgraphviz=enabled,-Dgraphviz=disabled,graphviz"
PACKAGECONFIG[jpeg] = "-Dlibjpeg=enabled,-Dlibjpeg=disabled,jpeg"
PACKAGECONFIG[lcms] = "-Dlcms=enabled,-Dlcms=disabled,lcms"
PACKAGECONFIG[libav] = "-Dlibav=enabled,-Dlibav=disabled,libav"
PACKAGECONFIG[libpng] = "-Dlibpng=enabled,-Dlibpng=disabled,libpng"
PACKAGECONFIG[librsvg] = "-Dlibrsvg=enabled,-Dlibrsvg=disabled,librsvg"
PACKAGECONFIG[sdl] = "-Dsdl1=enabled,-Dsdl1=disabled,virtual/libsdl"
PACKAGECONFIG[sdl2] = "-Dsdl2=enabled,-Dsdl2=disabled,virtual/libsdl2"
PACKAGECONFIG[tiff] = "-Dlibtiff=enabled,-Dlibtiff=disabled,tiff"
PACKAGECONFIG[webp] = "-Dwebp=enabled,-Dwebp=disabled,webp"

# There are a couple of non-symlink .so files installed into libdir, which need to go into main package
FILES_${PN} += " \
    ${libdir}/*.so \
    ${libdir}/gegl-${SHPV}/*.json \
    ${libdir}/gegl-${SHPV}/*.so \
"
FILES_SOLIBSDEV = "${libdir}/libgegl-${SHPV}${SOLIBSDEV}"

# Fails to build with thumb-1 (qemuarm)
# gegl-0.2.0/operations/common/matting-global.c: In function 'matting_process':
# gegl-0.2.0/operations/common/matting-global.c:463:1: internal compiler error: in patch_jump_insn, at cfgrtl.c:1275
ARM_INSTRUCTION_SET = "arm"

BBCLASSEXTEND = "native"
