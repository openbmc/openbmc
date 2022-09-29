FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}-5.15:"

SRC_URI:append:qemuarm64-secureboot = " \
    file://skip-unavailable-memory.patch \
    "

FFA_TEE_INCLUDE = "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', 'arm-ffa-5.15.inc', '' , d)}"
require ${FFA_TEE_INCLUDE}
