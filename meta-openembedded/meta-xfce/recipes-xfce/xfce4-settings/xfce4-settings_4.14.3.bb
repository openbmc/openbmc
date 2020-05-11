SUMMARY = "Xfce4 settings"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo exo-native garcon libxi virtual/libx11 xrandr libxcursor libxklavier upower"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-xsettings.xml-Set-default-themes.patch \
"
SRC_URI[md5sum] = "fe8f7d4221be1467b85c8539344c3f07"
SRC_URI[sha256sum] = "cab1a4d5351f9871533700523570f86f92bbe6e4055f44e5df950eb4b4f48bb3"

EXTRA_OECONF += "--enable-maintainer-mode --disable-debug"

PACKAGECONFIG ??= " \
    ${@bb.utils.contains('DISTRO_FEATURES','alsa','sound-setter', bb.utils.contains('DISTRO_FEATURES','pulseaudio','sound-setter','',d),d)} \
"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[sound-setter] = "--enable-sound-settings, --disable-sound-settings, libcanberra, libcanberra-gtk2 sound-theme-freedesktop"

FILES_${PN} += " \
    ${libdir}/xfce4 \
    ${datadir}/xfce4 \
"

RRECOMMENDS_${PN} += "adwaita-icon-theme"
RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','alsa','libcanberra-alsa','',d)}"
RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','pulseaudio','libcanberra-pulse','',d)}"
RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','systemd','xfce4-datetime-setter','',d)}"
