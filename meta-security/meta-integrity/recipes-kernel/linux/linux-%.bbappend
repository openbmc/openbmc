FILESEXTRAPATHS_prepend := "${THISDIR}/linux:"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'ima', ' file://ima.cfg', '', d)}"
