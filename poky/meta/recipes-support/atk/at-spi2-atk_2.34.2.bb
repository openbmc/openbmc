SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "58cd278574e101363b18d9a8b7053d67"
SRC_URI[archive.sha256sum] = "901323cee0eef05c01ec4dee06c701aeeca81a314a7d60216fa363005e27f4f0"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core libxml2"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase features_check upstream-version-is-even

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"

BBCLASSEXTEND = "native nativesdk"

CFLAGS += "-fcommon"
