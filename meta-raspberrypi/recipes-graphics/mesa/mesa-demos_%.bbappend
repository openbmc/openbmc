# mesa-demos need libgles1 and userland driver does not have it
COMPATIBLE_HOST_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '(.*)', 'null', d)}"
