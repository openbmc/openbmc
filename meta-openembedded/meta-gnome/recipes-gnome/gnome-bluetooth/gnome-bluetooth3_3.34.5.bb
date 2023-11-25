SUMMARY = "GNOME bluetooth manager"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SECTION = "x11/gnome"

DEPENDS = "udev gtk+3 libnotify libcanberra bluez5"

GNOMEBN = "gnome-bluetooth"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

GTKDOC_MESON_OPTION = "gtk_doc"

inherit features_check gnomebase gtk-icon-cache gtk-doc gobject-introspection upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

# gtk-icon-cache bbclass will take care of this for us.
EXTRA_OEMESON = "-Dicon_update=false"

SRC_URI += " file://0001-build-Fix-build-for-newer-versions-of-meson.patch"
SRC_URI[archive.md5sum] = "d83faa54abaf64bb40b5313bc233e74e"
SRC_URI[archive.sha256sum] = "6c949e52c8becc2054daacd604901f66ce5cf709a5fa91c4bb7cacc939b53ea9"

# avoid clashes with gnome-bluetooth
do_install:append() {
    # just bluetooth-sendto / bluetooth-sendto.desktop only
    rm -rf ${D}${bindir}
    rm -rf ${D}${datadir}/applications
}

FILES:${PN} += "${datadir}/gnome-bluetooth"

# offer alternate bluetooth-sendto
RRECOMMENS:${PN} += "gnome-bluetooth"

RDEPENDS:${PN} += "bluez5"
