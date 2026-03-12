SUMMARY = "Windowing concept abstraction library for X11 and Wayland"
HOMEPAGE = "https://docs.xfce.org/xfce/libxfce4windowing/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=435ed639f84d4585d93824e7da3d85da"

DEPENDS = "xfce4-dev-tools-native glib-2.0 gtk+3"

XFCEBASEBUILDCLASS = "meson"
SRC_URI[sha256sum] = "6b4e19a66db650c9c8a88f00bbf266e9ced0070b7dbc0aaeea05be0fc6a2eb4d"

inherit features_check gobject-introspection gtk-doc xfce vala gettext

# Currently, X11 is fully supported. Wayland is partially supported
ANY_OF_DISTRO_FEATURES = "wayland x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"
PACKAGECONFIG[x11] = "-Dx11=enabled, -Dx11=disabled, libx11 libdisplay-info libwnck3 xrandr"
PACKAGECONFIG[wayland] = "-Dwayland=enabled, -Dwayland=disabled, wayland wayland-native wayland-protocols"

GTKDOC_MESON_OPTION = "gtk-doc"
