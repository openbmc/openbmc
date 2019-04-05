FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-5.0:"

SRC_URI += "\
        ${@bb.utils.contains('DISTRO_FEATURES', 'apparmor', ' file://apparmor.cfg', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'apparmor', ' file://apparmor_on_boot.cfg', '', d)} \
"

SRC_URI += "\
        ${@bb.utils.contains('DISTRO_FEATURES', 'smack', ' file://smack.cfg', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'smack', ' file://smack-default-lsm.cfg', '', d)} \
"
