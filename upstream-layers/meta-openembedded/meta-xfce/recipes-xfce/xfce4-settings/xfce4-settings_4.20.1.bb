SUMMARY = "Xfce4 settings"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-settings/start"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo garcon libxi virtual/libx11 xrandr libxcursor libxklavier upower"

inherit xfce features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "fd0d602853ea75d94024e5baae2d2bf5ca8f8aa4dad7bfd5d08f9ff8afee77b2"

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
PACKAGECONFIG[sound-setter] = "--enable-sound-settings, --disable-sound-settings, libcanberra, libcanberra sound-theme-freedesktop"
PACKAGECONFIG[wayland] = "--enable-wayland, --disable-wayland, wayland-native"

FILES:${PN} += " \
    ${libdir}/xfce4 \
    ${libdir}/gtk-3.0/modules/libxfsettingsd-gtk-settings-sync.so \
    ${datadir}/xfce4 \
"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
RRECOMMENDS:${PN} += "${@bb.utils.contains_any('DISTRO_FEATURES','alsa pulseaudio','libcanberra','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','xfce4-datetime-setter','',d)}"
