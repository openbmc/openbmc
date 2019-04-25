SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+ glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[md5sum] = "0354811cd2622d3de92e342c7eaa184d"
SRC_URI[sha256sum] = "bf35b5432cb920987c6d7ff193600e5dd4d73422b6aa02b4ec5288744053b38c"

do_compile_prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
