SUMMARY = "Take photos and videos with your webcam, with fun graphical effects"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a17cb0a873d252440acfdf9b3d0e7fbf"

inherit gnomebase gobject-introspection vala itstool gtk-icon-cache gsettings gnome-help gtk-doc

SRC_URI += "file://0001-libcheese-Add-GtkWidget-cast-to-avoid-an-incompatibl.patch"

SRC_URI[archive.sha256sum] = "5f2185c4c99e54ddf2b8baf60c82819950e54952e132e8639875f3edcbf8f68e"

DEPENDS += " \
    clutter-1.0 \
    clutter-gst-3.0 \
    clutter-gtk-1.0 \
    gnome-desktop \
    libcanberra \
    libxslt-native \
"

GTKDOC_MESON_OPTION = "gtk_doc"

# Man page build wants to access sourceforge
EXTRA_OEMESON += "-Dman=false"

FILES:${PN} += "${datadir}"
