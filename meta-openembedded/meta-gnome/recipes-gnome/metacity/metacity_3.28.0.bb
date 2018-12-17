SECTION = "x11/wm"
SUMMARY = "Metacity is the boring window manager for the adult in you"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b4cce53560b8e619ffa7c830fb8761aa \
                    file://src/include/main.h;endline=24;md5=72148ede07a6dadd01de6a882d20a9ad"

PE = "1"

DEPENDS = "gsettings-desktop-schemas startup-notification \
           gnome-doc-utils gdk-pixbuf-native \
           gtk+3 glib-2.0 libcanberra libgtop intltool-native"

inherit autotools gettext gnomebase distro_features_check
# depends on startup-notification which depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://github.com/GNOME/metacity.git;branch=master \
           file://0001-drop-zenity-detection.patch \
"

S = "${WORKDIR}/git"
SRCREV = "c0d4b2fc0fcd6f2d3c37da935923f9e9ed5eb99f"

EXTRA_OECONF += "--disable-xinerama"

PACKAGECONFIG ?= ""

# enable as neccessary until new warnings are dealt with
PACKAGECONFIG[werror] = "--enable-Werror,--disable-Werror,,"

do_configure_prepend() {
    cd ${S}
    aclocal --install || exit 1
    autoreconf --verbose --force --install -Wno-portability || exit 1
    cd -

}

FILES_${PN} += "${datadir}/themes ${datadir}/gnome-control-center ${datadir}/gnome"
RDEPENDS_${PN} += "gsettings-desktop-schemas"

