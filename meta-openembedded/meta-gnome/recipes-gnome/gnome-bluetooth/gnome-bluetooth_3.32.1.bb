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

SRC_URI[archive.md5sum] = "5b3d66f564a5067ea154750cdb6d850d"
SRC_URI[archive.sha256sum] = "6441cafd313af94fba28b701698074f97d693b9023788a74e8e6f16f817ba1aa"

RDEPENDS_${PN} += "bluez5"
