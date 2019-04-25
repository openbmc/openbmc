SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886"

SRC_URI[archive.md5sum] = "1ad754b90bcb14244b73ca4d0c14d274"
SRC_URI[archive.sha256sum] = "e2e1571004ea7b105c969473ce455a95be4038fb2541471714aeb33a26da8a9a"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core libxml2"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase distro_features_check upstream-version-is-even

# The at-spi2-core requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"

BBCLASSEXTEND = "native nativesdk"
