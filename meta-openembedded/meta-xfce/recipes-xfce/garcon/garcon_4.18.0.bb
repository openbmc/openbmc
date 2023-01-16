DESCRIPTION = "Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4ui intltool-native"

inherit xfce gtk-doc gobject-introspection features_check

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[sha256sum] = "54633487566a8b8502b71c11a7f719efe27c069bd5773cc95f11ff4ea8f11a14"

EXTRA_OECONF = "--disable-gtk-doc"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/garcon/.libs"
}

FILES:${PN} += "${datadir}/desktop-directories"
