FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "${@bb.utils.contains("MACHINE_FEATURES", "tpm2", "file://measured-boot.cfg", "", d)}"
