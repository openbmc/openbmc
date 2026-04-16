SUMMARY = "Pulseaudio mixer for the xfce panel"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-pulseaudio-plugin/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f5eac6bb0d6ec0dc655e417781d4015f"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin features_check

REQUIRED_DISTRO_FEATURES = "pulseaudio"

DEPENDS += "dbus-glib pulseaudio"

SRC_URI[sha256sum] = "8e1f3a505f37aa3bc2816a58bec5f9555366f1c476f10eab59fd0e6581d08c47"

PACKAGECONFIG ??= "libnotify"
PACKAGECONFIG[libnotify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[libcanberra] = "--enable-libcanberra,--disable-libcanberra,libcanberra"

EXTRA_OECONF = "GLIB_COMPILE_RESOURCES=${STAGING_BINDIR_NATIVE}/glib-compile-resources"

RRECOMMENDS:${PN} = "${@bb.utils.contains_any('DISTRO_FEATURES', 'opengl vulkan', 'pavucontrol', '', d)}"
