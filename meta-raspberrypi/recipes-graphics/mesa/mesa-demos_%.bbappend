# mesa-demos need libgles1 and userland driver does not have it
COMPATIBLE_HOST:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '(.*)', 'null', d)}"
