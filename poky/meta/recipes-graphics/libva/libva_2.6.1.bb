require libva.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=2e48940f94acb0af582e5ef03537800f"
SRC_URI[md5sum] = "aef13eb48e01a47d1416d97462a22a11"
SRC_URI[sha256sum] = "6c57eb642d828af2411aa38f55dc10111e8c98976dbab8fd62e48629401eaea5"

PACKAGECONFIG ??= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'glx', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)} \
"

PACKAGECONFIG[x11] = "-Dwith_x11=yes,-Dwith_x11=no,virtual/libx11 libxext libxfixes"
PACKAGECONFIG[glx] = "-Dwith_glx=yes,-Dwith_glx=no,virtual/mesa"

PACKAGECONFIG[wayland] = "-Dwith_wayland=yes,-Dwith_wayland=no,wayland-native wayland"

PACKAGES =+ "${PN}-x11 ${PN}-glx ${PN}-wayland"

RDEPENDS_${PN}-x11 =+ "${PN}"
RDEPENDS_${PN}-glx =+ "${PN}-x11"

FILES_${PN}-x11 =+ "${libdir}/libva-x11*${SOLIBS}"
FILES_${PN}-glx =+ "${libdir}/libva-glx*${SOLIBS}"
FILES_${PN}-wayland =+ "${libdir}/libva-wayland*${SOLIBS}"
