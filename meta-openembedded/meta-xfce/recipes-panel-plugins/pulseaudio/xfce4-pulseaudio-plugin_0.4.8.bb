SUMMARY = "Pulseaudio mixer for the xfce panel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

inherit xfce-panel-plugin features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio x11"

DEPENDS += "dbus-glib pulseaudio"

SRC_URI += "file://0001-Use-new-xfw_window_activate-signature-in-libwindowin.patch"
SRC_URI[sha256sum] = "bd742b207c39c221e91c57c9c9be2839eb802d1b1ee01a02b7427cd02d3f0348"

PACKAGECONFIG ??= "libnotify"
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"

RRECOMMENDS:${PN} = "pavucontrol"
