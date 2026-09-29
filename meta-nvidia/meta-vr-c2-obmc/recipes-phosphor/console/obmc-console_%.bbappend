FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

OBMC_CONSOLE_HOST_TTY = "ttyS2"

SRC_URI:append = " file://server.ttyS2.conf"
