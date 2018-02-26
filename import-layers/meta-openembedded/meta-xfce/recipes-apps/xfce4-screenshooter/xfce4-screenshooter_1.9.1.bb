SUMMARY = "Application to take screenshots"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-screenshooter"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d791728a073bc009b4ffaf00b7599855"
DEPENDS = "xfce4-panel libxfce4util libxfce4ui gdk-pixbuf gtk+ glib-2.0 libsoup-2.4 exo libxfixes xext virtual/libx11"

inherit xfce-app

SRC_URI[md5sum] = "75e1501418f904769e4fd25eff6a3946"
SRC_URI[sha256sum] = "e62b31d9cf06a7414a26400c2ebe7a2ae7c2b22aa60f997f25145ea9ebe6e0db"

do_compile_prepend() {
    mkdir -p lib
    mkdir -p src
}

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/xfce4/panel/plugins \
    ${libdir}/xfce4/panel/plugins \
"
