SUMMARY = "Tecla is a keyboard layout viewer that uses GTK/Libadwaita for UI, and libxkbcommon to deal with keyboard maps."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=75859989545e37968a99b631ef42722e"

DEPENDS = " \
    libxkbcommon \
    libadwaita \
    wayland \
    gtk4 \
"

REQUIRED_DISTRO_FEATURES = "wayland"

inherit gnomebase pkgconfig  features_check

SRC_URI = "https://download.gnome.org/sources/tecla/${@oe.utils.trim_version('${PV}', 1)}/tecla-${PV}.tar.xz"
SRC_URI[sha256sum] = "783d3464d2a2cf7eb1507649dbd9ff09ce24852c2a6c9a0d365db84063d3d401"

