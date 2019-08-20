SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin distro_features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "dbus-glib pulseaudio"

SRC_URI[md5sum] = "9a34eadf06ed217ec0732a096f178987"
SRC_URI[sha256sum] = "4ae8aebc2458675d4f885bf16f73829be359e16370a684301bbef6a23758a120"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS_${PN} = "pavucontrol"
