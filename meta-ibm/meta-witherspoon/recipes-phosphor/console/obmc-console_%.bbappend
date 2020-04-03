FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_remove = "file://${BPN}.conf"
SRC_URI += "file://server.ttyVUART0.conf"
