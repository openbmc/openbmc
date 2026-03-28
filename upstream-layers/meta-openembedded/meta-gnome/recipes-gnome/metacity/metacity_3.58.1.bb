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

SRC_URI[archive.sha256sum] = "e430c8a92409ef2f91a4dabd50a71e3d3bbcc474a3dec1caec3813b381d7d1b0"

PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama"
# enable as neccessary until new warnings are dealt with
PACKAGECONFIG[werror] = "--enable-Werror,--disable-Werror,,"

FILES:${PN} += " \
    ${datadir}/themes \
    ${datadir}/gnome-control-center \
    ${datadir}/gnome\
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "gsettings-desktop-schemas"
