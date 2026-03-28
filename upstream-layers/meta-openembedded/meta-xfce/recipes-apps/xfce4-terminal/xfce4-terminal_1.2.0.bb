SUMMARY = "Terminal emulator for the Xfce desktop environment"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-terminal/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0 gtk+3 vte libxfce4ui libxslt-native docbook-xsl-stylesheets-native gtk-doc-native"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-app

SRC_URI += "\
            file://0001-build-Properly-guard-wayland-code.patch \
            file://0001-build-Do-not-display-full-path-in-generated-headers.patch \
            "
SRC_URI[sha256sum] = "6874c7b975cc3dc3bd636d57ffec723de7458202defe65377593d3a7e0734b94"

FILES:${PN} += " \
    ${datadir}/xfce4 \
    ${datadir}/gnome-control-center \
"

RRECOMMENDS:${PN} += "vte-prompt"
