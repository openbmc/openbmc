SUMMARY = "Pulseaudio mixer for the xfce panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-pulseaudio-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "dbus-glib pulseaudio"

SRC_URI[sha256sum] = "a0807615fb2848d0361b7e4568a44f26d189fda48011c7ba074986c8bfddc99a"

PACKAGECONFIG ??= "libnotify"
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS:${PN} = "pavucontrol"
