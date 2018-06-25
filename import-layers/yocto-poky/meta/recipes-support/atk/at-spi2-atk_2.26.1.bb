SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "eeec6cead3350dca48a235271c105b3e"
SRC_URI[sha256sum] = "b4f0c27b61dbffba7a5b5ba2ff88c8cee10ff8dac774fa5b79ce906853623b75"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core"

inherit autotools pkgconfig distro_features_check upstream-version-is-even

# The at-spi2-core requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"
