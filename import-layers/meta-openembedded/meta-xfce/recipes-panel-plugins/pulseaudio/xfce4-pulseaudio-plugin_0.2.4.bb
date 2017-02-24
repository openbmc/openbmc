SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin distro_features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "pulseaudio"

SRC_URI[md5sum] = "e0ffde419fa030f1f9bd0b56e3264a1c"
SRC_URI[sha256sum] = "8d9330ddf1d44a864a36d566cce4b76d4f859c5984bba7653d7dc39aa24d5c3e"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS_${PN} = "pavucontrol"
