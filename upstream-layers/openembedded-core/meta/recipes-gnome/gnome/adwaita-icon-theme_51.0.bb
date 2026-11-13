SUMMARY = "GTK+ icon theme"
DESCRIPTION = "The Adwaita icon theme is the default icon theme of the GNOME desktop \
This package package contains an icon theme for Gtk+ 3 applications."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/adwaita-icon-theme"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/adwaita-icon-theme/issues"
SECTION = "x11/gnome"

LICENSE = "CC-BY-SA-3.0 OR LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=c84cac88e46fc07647ea07e6c24eeb7c \
                    file://COPYING_CCBYSA3;md5=96143d33de3a79321b1006c4e8ed07e7 \
                    file://COPYING_LGPL;md5=e6a600fd5e1d9cbde2d983680233ad02"

inherit gnomebase allarch gtk-icon-cache

SRC_URI[archive.sha256sum] = "ba561cf3c96305a47179fa5605856ad695c9238ea07f7093aebd9ba80c4e323b"

PACKAGES =+ "${PN}-cursors ${PN}-symbolic"

RREPLACES:${PN} = "gnome-icon-theme"
RCONFLICTS:${PN} = "gnome-icon-theme"
RPROVIDES:${PN} = "gnome-icon-theme"

FILES:${PN}-cursors = "${datadir}/icons/Adwaita/cursors/"
FILES:${PN}-symbolic = "${datadir}/icons/Adwaita/symbolic*/"
FILES:${PN}-doc += "${datadir}/licenses/adwaita-icon-theme"

BBCLASSEXTEND = "native nativesdk"
