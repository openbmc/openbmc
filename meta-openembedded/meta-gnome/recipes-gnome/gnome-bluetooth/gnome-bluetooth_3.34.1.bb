SUMMARY = "GNOME bluetooth manager"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SECTION = "x11/gnome"

DEPENDS = "udev gtk+3 libnotify libcanberra bluez5"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"

inherit features_check gnomebase gtk-icon-cache gtk-doc gobject-introspection upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

# gtk-icon-cache bbclass will take care of this for us.
EXTRA_OEMESON = "-Dicon_update=false"

SRC_URI[archive.md5sum] = "09b6bab7ceaafb35da766a5476fbc466"
SRC_URI[archive.sha256sum] = "3ec91076c2822cd1f9abdc8e27663c3bda4c9c8a7a9773f9e92bfbf2b745d386"

RDEPENDS_${PN} += "bluez5"
