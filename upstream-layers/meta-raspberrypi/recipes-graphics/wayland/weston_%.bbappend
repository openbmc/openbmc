PACKAGECONFIG:remove:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', 'egl clients', d)}"

FILESEXTRAPATHS:prepend := "${THISDIR}/weston:"
SRC_URI:append:rpi = " file://0001-Adapt-weston-to-64-bit-plane-IDs.patch"
