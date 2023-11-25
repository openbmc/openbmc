SUMMARY = "Application to take screenshots"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-screenshooter/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+3 glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11 libxml-parser-perl-native"

inherit xfce-app perlnative

SRC_URI[sha256sum] = "a2f199687e54e16a936d5636d660d42b6b9a5d548cdd0f04bd69213554806494"

do_compile:prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
