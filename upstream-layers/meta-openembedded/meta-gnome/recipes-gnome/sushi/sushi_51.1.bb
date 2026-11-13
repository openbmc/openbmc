SUMMARY = "Quick previewer for Nautilus"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/sushi"
SECTION = "x11/gnome"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8a935b4d9a4fa8aa5978b50a432f833e"

DEPENDS = " \
    blueprint-compiler-native \
    freetype \
    fribidi \
    gjs \
    glib-2.0 \
    glycin \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gtk4 \
    gtksourceview5 \
    harfbuzz \
    libadwaita \
    papers \
    webkitgtk \
"

inherit gnomebase gobject-introspection gettext features_check

GIR_MESON_OPTION = ""

REQUIRED_DISTRO_FEATURES = "opengl gobject-introspection-data"

SRC_URI += "file://0001-libsushi-guard-the-X11-Window-field-when-GDK_WINDOW.patch"

SRC_URI[archive.sha256sum] = "d326ec69fc004be3a5fc048f7ecf2c8eb828b7d8b682f1e7a2ba01a5e72993e1"

export GI_TYPELIB_PATH = "${STAGING_LIBDIR}/girepository-1.0/"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled"
PACKAGECONFIG[x11] = "-DX11=enabled,-DX11=disabled,virtual/libx11"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
env = '${base_bindir}/env'
gjs = '${bindir}/gjs'
EOF
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/sushi \
"

RDEPENDS:${PN} += "gjs glycin-gtk4 gstreamer1.0-plugins-base gtksourceview5 libadwaita webkitgtk"
