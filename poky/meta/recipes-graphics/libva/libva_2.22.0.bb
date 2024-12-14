require libva.inc

PACKAGECONFIG ??= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'glx', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)} \
"

PACKAGES =+ "${PN}-x11 ${PN}-glx ${PN}-wayland"

RDEPENDS:${PN}-x11 =+ "${PN}"
RDEPENDS:${PN}-glx =+ "${PN}-x11"

FILES:${PN}-x11 =+ "${libdir}/libva-x11*${SOLIBS}"
FILES:${PN}-glx =+ "${libdir}/libva-glx*${SOLIBS}"
FILES:${PN}-wayland =+ "${libdir}/libva-wayland*${SOLIBS}"
