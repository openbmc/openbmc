SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin distro_features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "pulseaudio"

SRC_URI[md5sum] = "7df7280c19c2c8b8c5bc4f4f2136d1dd"
SRC_URI[sha256sum] = "6ca88314dbac3e24c0e1bfc593fad6edb66319766be62e8256c81b0314f049f0"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS_${PN} = "pavucontrol"
