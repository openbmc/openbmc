FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"

# Default service instance to install (single-host mode)
DEFAULT_INSTANCE = "ttyS0"

SRC_URI_remove = "file://${BPN}.conf"
SRC_URI += "file://ttyS0.conf"

