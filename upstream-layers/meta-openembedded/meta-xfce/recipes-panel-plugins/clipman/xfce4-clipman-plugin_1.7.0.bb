SUMMARY = "Clipman is a clipboard manager for Xfce"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-clipman-plugin/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit xfce-panel-plugin

DEPENDS += "xfconf xorgproto libxtst glib-2.0-native"

XFCEBASEBUILDCLASS = "meson"
XFCE_COMPRESS_TYPE = "xz"
SRC_URI[sha256sum] = "903302c3070a951d44ee17a84fa3cf21d36c6787678af8fbfc79e469fd00cb46"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)} \
"

PACKAGECONFIG[ptest] = "-Dtests=true,-Dtests=false,"
PACKAGECONFIG[qrencode] = "-Dqrencode=enabled,-Dqrencode=disabled,qrencode"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,wayland-native wayland"

FILES:${PN} += "${datadir}/metainfo"
