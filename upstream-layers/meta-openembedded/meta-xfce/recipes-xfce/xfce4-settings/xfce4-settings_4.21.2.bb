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

SRC_URI[sha256sum] = "064f72fb5ead25d9bd1724bea803f1017fc2287b32dc55eacf4f6e3d78e2b7e8"

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
