SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.sha256sum] = "cfa008a5af822b36ae6287f18182c40c91dd699c55faa38605881ed175ca464f"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core libxml2"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"

BBCLASSEXTEND = "native nativesdk"
