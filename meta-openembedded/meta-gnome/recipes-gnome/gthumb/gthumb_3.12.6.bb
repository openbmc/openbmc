SUMMARY = "Image viewer and browser"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    bison-native \
    gtk+3 \
    gsettings-desktop-schemas \
    zlib \
    jpeg \
    json-glib \
"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'colord', '', d)} exiv2 gstreamer lcms libjxl libraw librsvg libwebp"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=true,-Dgstreamer=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[libwebp] = "-Dlibwebp=true,-Dlibwebp=false,libwebp"
PACKAGECONFIG[libjxl] = "-Dlibjxl=true,-Dlibjxl=false,libjxl"
PACKAGECONFIG[lcms] = "-Dlcms2=true,-Dlcms2=false,lcms"
PACKAGECONFIG[colord] = "-Dcolord=true,-Dcolord=false,colord"
PACKAGECONFIG[exiv2] = "-Dexiv2=true,-Dexiv2=false,exiv2"
PACKAGECONFIG[librsvg] = "-Dlibrsvg=true,-Dlibrsvg=false,librsvg"
PACKAGECONFIG[libraw] = "-Dlibraw=true,-Dlibraw=false,libraw"

# webservices would require libsecret and webkitgtk3 built with deprecated libsoup2
EXTRA_OEMESON += "-Dwebservices=false -Dlibsecret=false"

inherit gnomebase gnome-help gsettings itstool mime-xdg

SRC_URI += "file://283c9483afd1215378c8b9871f51cd61770e53e7.patch"
SRC_URI[archive.sha256sum] = "608770c6c8e73073a1d404b65bd1b761e1ac5dc25e70130ff072488fa92f5c33"

FILES:${PN} += "${datadir}/metainfo"
