PACKAGECONFIG_zynqmp += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xcb', '', d)} \
                         egl glesv2"
