SECTION = "x11/wm"
SUMMARY = "Metacity is the boring window manager for the adult in you"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b4cce53560b8e619ffa7c830fb8761aa \
                    file://src/include/main.h;endline=24;md5=72148ede07a6dadd01de6a882d20a9ad"

PE = "1"

DEPENDS = " \
    gdk-pixbuf-native \
    gtk+3 \
    gsettings-desktop-schemas \
    startup-notification \
    libcanberra \
    libgtop \
    libxres \
    libxpresent \
"


# depends on startup-notification which depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

inherit gnomebase gsettings gettext upstream-version-is-even features_check

SRC_URI[archive.sha256sum] = "224c1f65487eac21f0c1d9856152343768ee726c48b1a8a2835a46a8ad9015b5"
SRC_URI += " \
    file://0001-drop-zenity-detection.patch \
    file://0001-fix-build-with-disable-dependency-tracking.patch \
"

PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama"
# enable as neccessary until new warnings are dealt with
PACKAGECONFIG[werror] = "--enable-Werror,--disable-Werror,,"

FILES:${PN} += " \
    ${datadir}/themes \
    ${datadir}/gnome-control-center \
    ${datadir}/gnome\
"

RDEPENDS:${PN} += "gsettings-desktop-schemas"
