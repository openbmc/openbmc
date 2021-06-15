SUMMARY = "GNOME Shell Extensions"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cb3a392cbf81a9e685ec13b88c4c101"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gsettings features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam gobject-introspection-data"

SRC_URI[archive.md5sum] = "d3a69cde0c3e3dc0b0c243af026c4b7a"
SRC_URI[archive.sha256sum] = "a1e16e75a06ea511435a6f7478de92aff21d02d4e1d59ec8ce7fb6396819b4b8"

DEPENDS += " \
    sassc-native \
"

EXTRA_OEMESON += " \
    -Dextension_set=all \
    -Dclassic_mode=true \
"

RDEPENDS_${PN} += "gnome-shell"

FILES_${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/gnome-session \
    ${datadir}/xsessions \
"
