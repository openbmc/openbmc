SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin distro_features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "pulseaudio"

SRC_URI[md5sum] = "12f3dd97e02c604e2023b5baf30d4a6c"
SRC_URI[sha256sum] = "aef9ad68a0b604a6aeca4c08146f3eb42bedb4fe1c8b65b82679cec0e2fc1cb3"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS_${PN} = "pavucontrol"
