SUMMARY = "Window navigation construction toolkit"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"


inherit gnomebase gsettings gobject-introspection gettext features_check upstream-version-is-even

DEPENDS = " \
    colord \
    geocode-glib \
    gcr \
    gnome-desktop \
    libgweather4 \
    lcms \
    libcanberra \
    geoclue \
    libnotify \
    upower \
    libwacom \
    networkmanager \
    alsa-lib \
"

# all these are mandatory
REQUIRED_DISTRO_FEATURES = "alsa polkit pulseaudio systemd gobject-introspection-data"
GIR_MESON_OPTION = ""

SRC_URI[archive.sha256sum] = "e7de8d30ae2d63c7a9a21f8f0247219275cbc1d7f5ed09205e4a47642666f6ae"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/gnome-settings-daemon/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xwayland', '', d)} \
    gudev \
    smartcard \
    cups \
"
PACKAGECONFIG[cups] = "-Dcups=true,-Dcups=false,cups"
PACKAGECONFIG[gudev] = "-Dgudev=true,-Dgudev=false,libgudev"
PACKAGECONFIG[smartcard] = "-Dsmartcard=true,-Dsmartcard=false,nss"
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dsystemd-units=true,-Dsystemd=false -Dsystemd-units=false,systemd"
PACKAGECONFIG[xwayland] = "-Dxwayland=true,-Dxwayland=false"

def gnome_verdir(v):
   return oe.utils.trim_version(v, 1)

FILES:${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/gnome-settings-daemon-${@gnome_verdir("${PV}")}/libgsd.so \
"
