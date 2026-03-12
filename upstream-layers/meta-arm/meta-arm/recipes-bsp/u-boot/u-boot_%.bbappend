FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:qemuarm64-secureboot = " file://qemuarm64.cfg"
SRC_URI:append:qemuarm-secureboot = " file://qemuarm.cfg"

require ${@bb.utils.contains('MACHINE_FEATURES', 'uefi-secureboot', 'u-boot-uefi-secureboot.inc', '', d)}

# Work some magic here for devupstream
BBCLASSEXTEND = "devupstream:target"
SRC_URI:class-devupstream = "git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master"
# tag: v2026.01-rc2
SRCREV:class-devupstream = "365a7079fb918643da0f0709660a7d8ea76dd6f3"
