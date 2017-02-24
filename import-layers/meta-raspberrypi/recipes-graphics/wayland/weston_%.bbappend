EXTRA_OECONF_append_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' --enable-rpi-compositor WESTON_NATIVE_BACKEND=rpi-backend.so', d)}"

PACKAGECONFIG_remove_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'fbdev', '', d)}"

EXTRA_OECONF += "--disable-xwayland-test \
                 --disable-simple-egl-clients \
"

EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', '--enable-rpi-compositor', d)}"
EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', '--disable-resize-optimization', d)}"
EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', '--disable-setuid-install', d)}"
EXTRA_OECONF += "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'WESTON_NATIVE_BACKEND=rpi-backend.so', d)}"
