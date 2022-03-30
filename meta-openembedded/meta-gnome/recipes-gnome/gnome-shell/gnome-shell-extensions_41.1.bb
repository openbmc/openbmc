SUMMARY = "GNOME Shell Extensions"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cb3a392cbf81a9e685ec13b88c4c101"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gsettings features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam gobject-introspection-data"

SRC_URI[archive.sha256sum] = "d0e6f2273f08d52d925fc2bb66b47b28e5ef50d1b8a14020877c662423d507d3"
SRC_URI += "file://0001-meson-Drop-unused-argument-for-i18n.merge_file.patch"

DEPENDS += " \
    sassc-native \
"

EXTRA_OEMESON += " \
    -Dextension_set=all \
    -Dclassic_mode=true \
"

do_install:append() {
    # enable gnome-classic session for wayland
    install -d ${D}${datadir}/wayland-sessions
    install -m644 ${D}${datadir}/xsessions/gnome-classic.desktop ${D}${datadir}/wayland-sessions/
}

RDEPENDS:${PN} += "gnome-shell"

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/gnome-session \
    ${datadir}/wayland-sessions \
    ${datadir}/xsessions \
"
