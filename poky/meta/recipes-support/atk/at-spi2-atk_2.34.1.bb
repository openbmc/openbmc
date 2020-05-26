SUMMARY = "AT-SPI 2 Toolkit Bridge"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "e0f99641c5a403041c4214be04722e15"
SRC_URI[archive.sha256sum] = "776df930748fde71c128be6c366a987b98b6ee66d508ed9c8db2355bf4b9cc16"

DEPENDS = "dbus glib-2.0 glib-2.0-native atk at-spi2-core libxml2"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase features_check upstream-version-is-even

# gnomebase.bbclass sets SRC_URI = , so we need to append after, at least for -native
SRC_URI += " file://0001-atk_test_util.h-add-missing-sys-time.h-include.patch"

PACKAGES =+ "${PN}-gnome ${PN}-gtk2"

FILES_${PN}-gnome = "${libdir}/gnome-settings-daemon-3.0/gtk-modules"
FILES_${PN}-gtk2 = "${libdir}/gtk-2.0/modules/libatk-bridge.*"

BBCLASSEXTEND = "native nativesdk"

CFLAGS += "-fcommon"
