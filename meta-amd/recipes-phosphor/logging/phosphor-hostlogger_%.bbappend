FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"

# Default service instance to install (single-host mode)
DEFAULT_INSTANCE:ethanolx = "ttyS0"

SRC_URI:remove = "file://${BPN}.conf"
SRC_URI:ethanolx += "file://ttyS0.conf"

