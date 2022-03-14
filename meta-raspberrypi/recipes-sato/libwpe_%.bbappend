# Workaround build issue with RPi userland EGL libraries.
CFLAGS:append:rpi = " ${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', '-D_GNU_SOURCE', d)}"
