SUMMARY = "Windowing concept abstraction library for X11 and Wayland"
HOMEPAGE = "https://docs.xfce.org/xfce/libxfce4windowing/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=435ed639f84d4585d93824e7da3d85da"

DEPENDS = "xfce4-dev-tools-native libdisplay-info libwnck3"

SRC_URI[sha256sum] = "df2419a6bd960c0bfac3307eff593050857524642597eb35a26fb4f8261a017b"

inherit features_check gobject-introspection gtk-doc xfce

# Currently, X11 is fully supported. Wayland is partially supported
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"
PACKAGECONFIG[wayland] = "--enable-wayland, --disable-wayland, wayland-native"

EXTRA_OECONF = "--enable-x11 \
                WAYLAND_SCANNER=${STAGING_BINDIR_NATIVE}/wayland-scanner \
                "

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH=${B}/libxfce4windowing/.libs
}
