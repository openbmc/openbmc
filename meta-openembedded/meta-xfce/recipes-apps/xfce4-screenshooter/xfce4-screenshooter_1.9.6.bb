SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+ glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[md5sum] = "9e4026d9ae7850b290992c9d2aea80dd"
SRC_URI[sha256sum] = "88c174ce687d1f7ba5470d6ab5784d33dc215f8f220211e892e268189dbea658"

do_compile_prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
