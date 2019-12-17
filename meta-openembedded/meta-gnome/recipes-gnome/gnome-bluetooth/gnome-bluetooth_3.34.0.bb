SUMMARY = "GNOME bluetooth manager"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SECTION = "x11/gnome"

DEPENDS = "udev gtk+3 libnotify libcanberra bluez5"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit gnomebase gtk-icon-cache gtk-doc gobject-introspection upstream-version-is-even

# gtk-icon-cache bbclass will take care of this for us.
EXTRA_OEMESON = "-Dicon_update=false"

SRC_URI[archive.md5sum] = "0c567e124a52e8ddc31c8bed0c3e57a1"
SRC_URI[archive.sha256sum] = "e867e67423e1dc78c56c2ea11dec066ce0254238d559e4777c80fa2935eb6baf"

RDEPENDS_${PN} += "bluez5"
