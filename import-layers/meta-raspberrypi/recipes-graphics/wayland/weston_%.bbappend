PACKAGECONFIG_remove_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'fbdev', '', d)}"

EXTRA_OECONF_append_rpi = " \
    --disable-xwayland-test \
    --disable-simple-egl-clients \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' \
        --disable-resize-optimization \
        --disable-setuid-install \
    ', d)} \
"
