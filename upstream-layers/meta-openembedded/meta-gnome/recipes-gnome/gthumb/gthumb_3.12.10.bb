SUMMARY = "Image viewer and browser"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    bison-native \
    desktop-file-utils-native \
    exiv2 \
    gtk+3 \
    gsettings-desktop-schemas \
    zlib \
    jpeg \
    json-glib \
"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'colord', '', d)} gstreamer lcms libjxl libraw librsvg libwebp"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=true,-Dgstreamer=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[libwebp] = "-Dlibwebp=true,-Dlibwebp=false,libwebp"
PACKAGECONFIG[libjxl] = "-Dlibjxl=true,-Dlibjxl=false,libjxl"
PACKAGECONFIG[lcms] = "-Dlcms2=true,-Dlcms2=false,lcms"
PACKAGECONFIG[colord] = "-Dcolord=true,-Dcolord=false,colord"
PACKAGECONFIG[librsvg] = "-Dlibrsvg=true,-Dlibrsvg=false,librsvg"
PACKAGECONFIG[libraw] = "-Dlibraw=true,-Dlibraw=false,libraw"

inherit gnomebase gnome-help gsettings itstool gtk-icon-cache mime-xdg
SRC_URI[archive.sha256sum] = "3222344653cd6fb5d7981b73951ae3d90c414f74220a8b1c8665b25574284c75"

FILES:${PN} += "${datadir}/metainfo"
