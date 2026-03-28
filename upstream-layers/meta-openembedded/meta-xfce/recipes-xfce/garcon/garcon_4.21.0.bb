DESCRIPTION = "Xfce Menu Library"
HOMEPAGE = "https://docs.xfce.org/xfce/garcon/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-only & GFDL-1.1-no-invariants-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=04a01abcbdabffae1ca26335a293276b"
DEPENDS = "xfce4-dev-tools-native libxfce4ui intltool-native"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce gtk-doc gobject-introspection features_check

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI += "file://0001-xfce-applications.menu-don-t-bloat-settings-menu-by-.patch"
SRC_URI[sha256sum] = "3acc3f6b81059199f4e6646da7b6ca39edf84ea90dd3ff87088ffca6aa108269"

EXTRA_OECONF = "--disable-gtk-doc"

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/garcon/.libs"
    # g-ir-scanner needs garcon/garcon-config.h but it's generated in ${B}/garcon/
    mkdir -p ${B}/garcon/garcon ${B}/garcon-gtk/garcon
    ln -sf ${B}/garcon/garcon-config.h ${B}/garcon/garcon/
    ln -sf ${B}/garcon/garcon-config.h ${B}/garcon-gtk/garcon/
}

FILES:${PN} += "${datadir}/desktop-directories"
