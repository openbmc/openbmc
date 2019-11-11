SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+ glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[md5sum] = "9b63c0fa7cbde8ec4e6cacb75fc44b65"
SRC_URI[sha256sum] = "0f7161053a23a6413376f4d17db6b774d4849384a9b1ffe01fdb2b0035a070d1"

do_compile_prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
