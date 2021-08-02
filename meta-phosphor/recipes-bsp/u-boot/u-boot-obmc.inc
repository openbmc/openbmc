FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = '${@bb.utils.contains_any("DISTRO_FEATURES", "disable-u-boot-shell", \
    " file://disable-u-boot-shell.cfg", \
    "", d)}'
