SUMMARY = "GNOME bluetooth manager"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SECTION = "x11/gnome"

DEPENDS = " \
    udev \
    libnotify \
    libcanberra \
    bluez5 \
    upower \
    gtk4 \
    gsound \
    libadwaita \
"

GNOMEBN = "gnome-bluetooth"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

GNOMEBASEBUILDCLASS = "meson"
GTKDOC_MESON_OPTION = "gtk_doc"
GTKIC_VERSION = "4"

inherit features_check gnomebase gtk-icon-cache gtk-doc gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "3d1e2720673bc9a242619dc5d231fd6e776c18a49da29f43cdc433055adb0170"

RDEPENDS:${PN} += "bluez5"

FILES:${PN} += "${datadir}/gnome-bluetooth-3.0"
