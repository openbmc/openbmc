FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:qemuarm64-secureboot = " file://qemuarm64.cfg"
SRC_URI:append:qemuarm-secureboot = " file://qemuarm.cfg"

require ${@bb.utils.contains('MACHINE_FEATURES', 'uefi-secureboot', 'u-boot-uefi-secureboot.inc', '', d)}
