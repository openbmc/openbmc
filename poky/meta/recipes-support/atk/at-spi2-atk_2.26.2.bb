SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886"

SRC_URI[archive.md5sum] = "355c7916a69513490cb83ad34016b169"
SRC_URI[archive.sha256sum] = "61891f0abae1689f6617a963105a3f1dcdab5970c4a36ded9c79a7a544b16a6e"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core libxml2"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase distro_features_check upstream-version-is-even

# The at-spi2-core requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"
