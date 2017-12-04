OPENGL_PKGCFG = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'dri3 xshmfence glamor', '', d)}"

# slightly modified to oe-core's default: add ${OPENGL_PKGCFG}
PACKAGECONFIG_rpi ?= " \
    dri2 udev ${XORG_CRYPTO} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri glx ${OPENGL_PKGCFG}', '', d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "xwayland", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd", "", d)} \
"

XSERVER_RRECOMMENDS_append = " xf86-input-libinput"
