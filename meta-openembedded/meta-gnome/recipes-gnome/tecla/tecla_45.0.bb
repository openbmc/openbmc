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
GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig  features_check

SRC_URI[archive.sha256sum] = "5c02bb4019b1cffb5663da6107503eff853836a8783dd4705dd04a49f7adc25b"
