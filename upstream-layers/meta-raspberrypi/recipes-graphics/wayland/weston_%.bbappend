PACKAGECONFIG:remove:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'fbdev', 'egl clients', d)}"

EXTRA_OECONF:append:rpi = " \
    --disable-xwayland-test \
    --disable-simple-egl-clients \
    ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' \
        --disable-resize-optimization \
        --disable-setuid-install \
    ', d)} \
"
