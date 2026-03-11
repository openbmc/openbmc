SUMMARY = "The GIMP is the GNU Image Manipulation Program"
HOMEPAGE = "http://www.gimp.org"
SECTION = "graphics"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

DEPENDS = " \
    appstream-glib \
    atk \
    babl \
    bzip2 \
    bison-native \
    cairo \
    fontconfig \
    freetype \
    gdk-pixbuf-native \
    gegl \
    gexiv2 \
    glib-2.0 \
    glib-2.0-native \
    gtk+3 \
    harfbuzz \
    iso-codes-native \
    jpeg \
    json-glib \
    lcms \
    libarchive \
    libexif \
    libmypaint \
    libpng \
    librsvg \
    librsvg-native \
    libxmu \
    libxslt-native \
    mypaint-brushes-1.0 \
    pango \
    poppler \
    poppler-data \
    python3-pygobject-native \
    tiff \
    xz \
    zlib \
"

DEPENDS:append:libc-musl = " libexecinfo"

LDFLAGS:append:libc-musl = " -lexecinfo"

inherit meson gtk-icon-cache mime-xdg pkgconfig gettext gobject-introspection vala

GIR_MESON_OPTION = 'can-crosscompile-gir'
VALA_MESON_OPTION = "vala"
VALA_MESON_ENABLE_FLAG = "enabled"
VALA_MESON_DISABLE_FLAG = "disabled"
GIDOCGEN_MESON_OPTION = "gi-docgen"
GIDOCGEN_MESON_ENABLE_FLAG = "enabled"
GIDOCGEN_MESON_DISABLE_FLAG = "disabled"

SRC_URI = "gitsm://gitlab.gnome.org/GNOME/gimp.git;protocol=https;branch=master"
SRC_URI += "file://0001-gimp-cross-compile-fix-for-bz2.patch"
SRC_URI += "file://0002-meson.build-reproducibility-fix.patch"
SRC_URI += "file://0001-meson.build-dont-check-for-lgi.patch"
SRC_URI += "file://0001-meson.build-require-iso-codes-native.patch"
SRCREV = "c1901c5be644ab8dcaf779b1f383bf5370bc90c6"


PACKAGECONFIG[aa] = "-Daa=enabled,-Daa=disabled,aalib"
PACKAGECONFIG[alsa] = "-Dalsa=enabled,-Dalsa=disabled,alsa-lib"
PACKAGECONFIG[appdata-test] = "-Dappdata-test=enabled,-Dappdata-test=disabled,appstream-native"
PACKAGECONFIG[cairo-pdf] = "-Dcairo-pdf=enabled,-Dcairo-pdf=disabled"
PACKAGECONFIG[check-update] = "-Dcheck-update=yes,-Dcheck-update=no"
PACKAGECONFIG[ghostscript] = "-Dghostscript=enabled,-Dghostscript=disabled,ghostscript,ghostscript"
PACKAGECONFIG[gudev] = "-Dgudev=enabled,-Dgudev=disabled,libgudev"
PACKAGECONFIG[heif] = "-Dheif=enabled,-Dheif=disabled,libheif"
PACKAGECONFIG[javascript] = "-Djavascript=enabled,-Djavascript=disabled,,gjs"
PACKAGECONFIG[jpeg2000] = "-Djpeg2000=enabled,-Djpeg2000=disabled,jasper"
PACKAGECONFIG[jpeg-xl] = "-Djpeg-xl=enabled,-Djpeg-xl=disabled,libjxl"
PACKAGECONFIG[libunwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"
PACKAGECONFIG[libbacktrace] = "-Dlibbacktrace=true,-Dlibbacktrace=false,libbacktrace"
PACKAGECONFIG[lua] = "-Dlua=true,-Dlua=false,,luajit lua-lgi"
PACKAGECONFIG[mng] = "-Dmng=enabled,-Dmng=disabled,libmng"
PACKAGECONFIG[openmp] = "-Dopenmp=enabled,-Dopenmp=disabled,gcc-runtime"
PACKAGECONFIG[vector-icons] = "-Dvector-icons=true,-Dvector-icons=false,librsvg shared-mime-info"
PACKAGECONFIG[webp] = "-Dwebp=enabled,-Dwebp=disabled,libwebp"
PACKAGECONFIG[xcursor] = "-Dxcursor=enabled,-Dxcursor=disabled,libxcursor"
PACKAGECONFIG[x11] = "-Dxpm=enabled,-Dxpm=disabled,libxpm libxext libxfixes"

PACKAGECONFIG ?= " \
    alsa \
    cairo-pdf \
    ghostscript \
    gudev \
    javascript \
    jpeg2000 \
    jpeg-xl \
    lua \
    mng \
    webp \
    ${@oe.utils.conditional('SITEINFO_BITS', '32', '', 'vector-icons', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xcursor', '', d)} \
"

PACKAGECONFIG:remove:riscv32 = "lua"
PACKAGECONFIG:remove:riscv64 = "lua"
PACKAGECONFIG:remove:powerpc64 = "lua"
PACKAGECONFIG:remove:powerpc64le = "lua"

EXTRA_OEMESON += " \
    -Dshmem-type=posix \
    -Dlinux-input=enabled \
    --buildtype release \
    --cross-file=${WORKDIR}/meson-${PN}.cross \
"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
gjs = '${bindir}/gjs'
luajit = '${bindir}/luajit'
EOF
}

do_configure:append () {
    sed -i -e "s|build_by_default: true|build_by_default: false|" ${S}/gimp-data/images/meson.build
}

do_install:prepend() {
    sed -i -e "s|${B}||" ${B}/app/widgets/gimplanguagestore-data.h
    sed -i -e "s|${B}||" ${B}/plug-ins/file-bmp/huffman.h
}

FILES:${PN} += "${datadir}/metainfo"

RDEPENDS:${PN} = "mypaint-brushes-1.0 glib-networking python3-pygobject"

CVE_STATUS[CVE-2007-3741] = "not-applicable-platform: This only applies for Mandriva Linux"
