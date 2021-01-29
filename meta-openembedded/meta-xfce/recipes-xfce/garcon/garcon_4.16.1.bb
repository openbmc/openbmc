DESCRIPTION = "Xfce Menu Library"
SECTION = "x11/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4ui intltool-native"

inherit xfce gtk-doc gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[sha256sum] = "84914927a2c1fda167f67af26a6640630a744a22940df508aa6c752cdbd3d21d"

EXTRA_OECONF = "--disable-gtk-doc"

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/garcon/.libs"
}

FILES_${PN} += "${datadir}/desktop-directories"
