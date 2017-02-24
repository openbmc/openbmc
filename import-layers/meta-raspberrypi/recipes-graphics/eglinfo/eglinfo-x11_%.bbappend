EGLINFO_DEVICE_rpi  = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'generic', 'raspberrypi', d)}"
ASNEEDED = ""
