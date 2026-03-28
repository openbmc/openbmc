SUMMARY = "Xfce4 Widget library and X Window System interaction"
HOMEPAGE = "https://docs.xfce.org/xfce/libxfce4ui/start"
SECTION = "x11/libs"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cf66a4984120007c9881cc871cf49db"
DEPENDS = "intltool-native perl-native gtk+3 libxfce4util xfce4-dev-tools xfconf"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce gtk-doc gobject-introspection features_check

# libxfce4ui uses 'gtk-doc' instead of 'docs' for meson option
GTKDOC_MESON_OPTION = "gtk-doc"

# xfce4 depends on libwnck3. gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'x11','opengl', "", d)}"

# TODO: Check if 0001-... can go
SRC_URI += "file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch \
            file://0001-build-Do-not-display-full-path-in-generated-headers.patch \
            "
SRC_URI[sha256sum] = "a72a7af39cf183819bcfb61b1747d425261e966ccb172b2fc28f1494f524bd17"

EXTRA_OEMESON = "-Dvala=disabled -Dvendor-info=${DISTRO}"

PACKAGECONFIG ??= " \
       ${@bb.utils.contains('DISTRO_FEATURES', 'x11','x11', "", d)} \
"
PACKAGECONFIG[x11] = "-Dstartup-notification=enabled,-Dstartup-notification=disabled,libepoxy libice libsm startup-notification"
