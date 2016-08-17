SUMMARY = "Xfce4 settings"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo exo-native garcon gtk+ libxfce4util libxfce4ui xfconf dbus-glib libxi virtual/libx11 xrandr libxcursor libxklavier upower"

inherit xfce xfce-git

# schnitzeltony git repo is the mainline repo
# + datetime-setter - sent to mainline but strange response
# + minor bugfixes - sent mainline but no response
# + option to hide mousepointer for a specific (touch) input device - sent mainline but no response
SRC_URI = " \
    git://github.com/schnitzeltony/xfce4-settings.git;protocol=git;branch=for-oe-4.12.0-1 \
    file://0001-xsettings.xml-Set-default-themes.patch \
"
SRCREV = "c6683cb2cff489c16c2c7b5eab4017bb461f07f1"
S = "${WORKDIR}/git"
PV = "4.12.0+git${SRCPV}"
 
EXTRA_OECONF += "--enable-maintainer-mode --disable-debug"

PACKAGECONFIG ??= " \
    ${@bb.utils.contains('DISTRO_FEATURES','systemd','datetime-setter','',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES','alsa','sound-setter', bb.utils.contains('DISTRO_FEATURES','pulseaudio','sound-setter','',d),d)} \
"
PACKAGECONFIG[datetime-setter] = "--enable-datetime-settings, --disable-datetime-settings,, tzdata"
PACKAGECONFIG[notify] = "--enable-libnotify,--disable-libnotify,libnotify"
PACKAGECONFIG[sound-setter] = "--enable-sound-settings, --disable-sound-settings, libcanberra, libcanberra-gtk2 sound-theme-freedesktop"

FILES_${PN} += " \
    ${libdir}/xfce4 \
    ${datadir}/xfce4 \
"

RRECOMMENDS_${PN} += "adwaita-icon-theme"
RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','alsa','libcanberra-alsa','',d)}"
RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES','pulseaudio','libcanberra-pulse','',d)}"
