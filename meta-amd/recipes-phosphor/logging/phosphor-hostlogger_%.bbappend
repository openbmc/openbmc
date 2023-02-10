FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"

# Default service instance to install (single-host mode)
DEFAULT_INSTANCE:ethanolx = "ttyS0"
DEFAULT_INSTANCE:daytonax = "ttyVUART0"

SRC_URI:remove = "file://${BPN}.conf"
SRC_URI:ethanolx += "file://ttyS0.conf"
SRC_URI:daytonax += "file://ttyVUART0.conf"
