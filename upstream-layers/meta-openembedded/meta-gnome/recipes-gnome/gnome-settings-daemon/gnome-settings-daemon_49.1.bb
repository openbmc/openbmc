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
"

# all these are mandatory
REQUIRED_DISTRO_FEATURES = "polkit pulseaudio systemd gobject-introspection-data"
GIR_MESON_OPTION = ""

SRC_URI += "file://0001-gsd-smartcard-enum-types.c.in-fix-reproducibility-is.patch"
SRC_URI[archive.sha256sum] = "2a9957fc4f91c3b9127b49484179bef485120d9c1c208e44d44e6a746e6cc1c1"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd x11 alsa', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xwayland', '', d)} \
    gudev \
    smartcard \
    cups \
"
PACKAGECONFIG[alsa] = "-Dalsa=true,-Dalsa=false,alsa-lib"
PACKAGECONFIG[cups] = "-Dcups=true,-Dcups=false,cups"
PACKAGECONFIG[gudev] = "-Dgudev=true,-Dgudev=false,libgudev"
PACKAGECONFIG[smartcard] = "-Dsmartcard=true,-Dsmartcard=false,nss"
PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[xwayland] = "-Dxwayland=true,-Dxwayland=false"
PACKAGECONFIG[x11] = "-Dx11=true,-Dx11=false,libx11 libxfixes"

def gnome_verdir(v):
   return oe.utils.trim_version(v, 1)

FILES:${PN} += " \
    ${systemd_user_unitdir} \
    ${libdir}/gnome-settings-daemon-${@gnome_verdir("${PV}")}/libgsd.so \
"
