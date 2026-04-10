PACKAGECONFIG:remove:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'egl clients', d)}"
