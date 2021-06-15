SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "dbus-glib pulseaudio"

SRC_URI[md5sum] = "3d86032acb9364d47e0a144350c63e1a"
SRC_URI[sha256sum] = "5a518237e2137341d8ca6584938950525e20c28a0177e30ecaea3ba8e7a2615b"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS_${PN} = "pavucontrol"
