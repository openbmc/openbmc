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

SRC_URI[archive.sha256sum] = "0790b99ec29137a54b546c510661a99aa6f039c8d75f10c08e928682c0804fe5"
