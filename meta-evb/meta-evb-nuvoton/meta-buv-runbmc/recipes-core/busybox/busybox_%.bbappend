FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"
SRC_URI:append:buv-runbmc = " file://busybox.cfg"
SRC_URI:append:buv-runbmc = "${@bb.utils.contains('DISTRO_FEATURES', 'buv-dev', ' file://buv-dev.cfg', '', d)}"
