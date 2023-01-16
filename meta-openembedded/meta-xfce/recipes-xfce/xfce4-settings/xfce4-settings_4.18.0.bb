SUMMARY = "Xfce4 settings"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo garcon libxi virtual/libx11 xrandr libxcursor libxklavier upower"

inherit xfce features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xsettings.xml-Set-default-themes.patch"
SRC_URI[sha256sum] = "ed3f75837cb33cd694610fc87cd569c4782b7ac4e099143a3dbe8fff1f1c6a9d"

EXTRA_OECONF += "--enable-maintainer-mode --disable-debug"

PACKAGECONFIG ??= " \
    notify \
    ${@bb.utils.contains('DISTRO_FEATURES','alsa','sound-setter', bb.utils.contains('DISTRO_FEATURES','pulseaudio','sound-setter','',d),d)} \
"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[sound-setter] = "--enable-sound-settings, --disable-sound-settings, libcanberra, libcanberra-gtk2 sound-theme-freedesktop"

FILES:${PN} += " \
    ${libdir}/xfce4 \
    ${datadir}/xfce4 \
"

RRECOMMENDS:${PN} += "adwaita-icon-theme"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','alsa','libcanberra-alsa','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','pulseaudio','libcanberra-pulse','',d)}"
RRECOMMENDS:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','xfce4-datetime-setter','',d)}"
