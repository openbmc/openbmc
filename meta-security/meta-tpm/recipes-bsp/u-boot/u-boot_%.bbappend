FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "${@bb.utils.contains("MACHINE_FEATURES", "measured-boot", "file://measured-boot.cfg", "", d)}"