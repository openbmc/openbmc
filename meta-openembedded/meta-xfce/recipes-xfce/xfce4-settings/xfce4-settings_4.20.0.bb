SUMMARY = "Xfce4 settings"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo garcon libxi virtual/libx11 xrandr libxcursor libxklavier upower"

inherit xfce features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "23548da3429a296501fbfdbc98a861ee241b9fdd47e8d5de1781f57c6bbce5a9"

CFLAGS += " -Wno-deprecated-declarations -Wno-implicit-function-declaration"
EXTRA_OECONF += " \
    GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen \
    --enable-maintainer-mode --disable-debug \
"

PACKAGECONFIG ??= " \
    notify \
    ${@bb.utils.contains('DISTRO_FEATURES','alsa','sound-setter', bb.utils.contains('DISTRO_FEATURES','pulseaudio','sound-setter','',d),d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[sound-setter] = "--enable-sound-settings, --disable-sound-settings, libcanberra, libcanberra-gtk2 sound-theme-freedesktop"
PACKAGECONFIG[wayland] = "--enable-wayland, --disable-wayland, wayland-native"

FILES:${PN} += " \
    ${libdir}/xfce4 \
    ${libdir}/gtk-3.0/modules/libxfsettingsd-gtk-settings-sync.so \
    ${datadir}/xfce4 \
"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','alsa','libcanberra-alsa','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','pulseaudio','libcanberra-pulse','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','xfce4-datetime-setter','',d)}"
