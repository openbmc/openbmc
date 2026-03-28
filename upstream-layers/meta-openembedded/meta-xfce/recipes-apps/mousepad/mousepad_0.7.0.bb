SUMMARY = "A simple text editor for Xfce"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 gtksourceview4 xfconf xfce4-dev-tools-native"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-app gsettings mime-xdg

SRC_URI[sha256sum] = "e86c59feb08126d4cace368432c16b2dee8e519aaca8a9d2b409ae1cdd200802"

PACKAGECONFIG ??= ""
PACKAGECONFIG[spell] = "-Dgspell-plugin=enabled,-Dgspell-plugin=disabled,gspell"

FILES:${PN} += " \
    ${datadir}/glib-2.0/schemas \
    ${datadir}/metainfo \
    ${datadir}/polkit-1 \
"
