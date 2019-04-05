SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+ glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[md5sum] = "68321e50939fea3c66f0771d59845dab"
SRC_URI[sha256sum] = "c4f3225b3f73f1feb1869807b8b27aec4c32a3e9c2954915a5fb39d18d3a60f2"

do_compile_prepend() {
    mkdir -p lib
    mkdir -p src panel-plugin
}

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
