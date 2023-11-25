SECTION = "x11/wm"
SUMMARY = "Metacity is the boring window manager for the adult in you"
LICENSE = "GPL-2.0-or-later"
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

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gsettings gettext upstream-version-is-even features_check

SRC_URI[archive.sha256sum] = "18e9b106438d46394e4148bcb83acc6367312be54559cdb564e270c1ccaeb60f"
SRC_URI += "file://0001-drop-zenity-detection.patch"

PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama"
# enable as neccessary until new warnings are dealt with
PACKAGECONFIG[werror] = "--enable-Werror,--disable-Werror,,"

FILES:${PN} += " \
    ${datadir}/themes \
    ${datadir}/gnome-control-center \
    ${datadir}/gnome\
"

RDEPENDS:${PN} += "gsettings-desktop-schemas"
