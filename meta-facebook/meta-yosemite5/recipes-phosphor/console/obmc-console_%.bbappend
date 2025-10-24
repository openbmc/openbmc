FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://server.ttyS3.conf \
    "

OBMC_CONSOLE_TTYS:append = " ttyS3"

