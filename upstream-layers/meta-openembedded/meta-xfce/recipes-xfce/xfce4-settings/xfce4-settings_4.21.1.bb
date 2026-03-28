SUMMARY = "Xfce4 settings"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-settings/start"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo garcon libxi virtual/libx11 xrandr libxcursor libxklavier upower"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-build-Do-not-display-full-path-in-generated-headers.patch"
SRC_URI[sha256sum] = "e57a33d0bb9d63d0ef6f469c2d42cbf66e37d9127cc204de411b7385274e26d0"

PACKAGECONFIG ??= " \
    notify \
    ${@bb.utils.contains('DISTRO_FEATURES','alsa','sound-setter', bb.utils.contains('DISTRO_FEATURES','pulseaudio','sound-setter','',d),d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
"
PACKAGECONFIG[notify] = "-Dlibnotify=enabled,-Dlibnotify=disabled,libnotify"
PACKAGECONFIG[sound-setter] = "-Dsound-settings=true,-Dsound-settings=false,libcanberra,libcanberra sound-theme-freedesktop"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,wayland-native"

FILES:${PN} += " \
    ${libdir}/xfce4 \
    ${libdir}/gtk-3.0/modules/libxfsettingsd-gtk-settings-sync.so \
    ${datadir}/xfce4 \
"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
RRECOMMENDS:${PN} += "${@bb.utils.contains_any('DISTRO_FEATURES','alsa pulseaudio','libcanberra','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','xfce4-datetime-setter','',d)}"
