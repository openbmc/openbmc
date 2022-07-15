SUMMARY = "A light and fast file manager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = " \
    glib-2.0-native \
    gtk+ \
"

inherit gnomebase itstool gettext gnome-help features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "78d8dce70fb922b2909cf767783053b0811213a4d6a49b3875510cf3a84efd7b"

PACKAGECONFIG ??= "exiv2 taglib libgsf poppler"
PACKAGECONFIG[exiv2] = "--with-exiv2,--without-exiv2,exiv2"
PACKAGECONFIG[taglib] = "--with-taglib,--without-taglib,taglib"
PACKAGECONFIG[libgsf] = "--with-libgsf,--without-libgsf,libgsf"
PACKAGECONFIG[poppler] = "--with-poppler,--without-poppler,poppler"

FILES:${PN} += "${datadir}/metainfo"
FILES:${PN}-dev += "${libdir}/${BPN}/lib*${SOLIBSDEV}"
