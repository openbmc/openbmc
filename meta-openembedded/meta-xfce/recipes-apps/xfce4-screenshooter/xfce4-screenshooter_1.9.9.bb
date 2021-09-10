SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+3 glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[sha256sum] = "33c8aeb53fbdb82dbd7d40bca77a8affbb4116ba0993cd59474b554558e5daa4"

do_compile:prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
