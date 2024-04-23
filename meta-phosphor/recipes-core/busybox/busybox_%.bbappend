FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://busybox.cfg \
    file://flash.cfg \
    file://less.cfg \
    file://mountpoint.cfg \
    file://top.cfg \
    ${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-fs', \
                         '', 'file://reboot.cfg', d)} \
"
