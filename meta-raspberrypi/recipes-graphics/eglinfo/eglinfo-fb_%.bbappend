EGLINFO_DEVICE_rpi  = "raspberrypi"
COMPATIBLE_HOST_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'null', '"(.*)"', d)}"
