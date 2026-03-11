# userland driver EGL implementation does not have all needed bits for it so remove it from build
COMPATIBLE_HOST:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '(.*)', 'null', d)}"
