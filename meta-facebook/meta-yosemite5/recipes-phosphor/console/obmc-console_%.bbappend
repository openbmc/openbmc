FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
RDEPENDS:${PN}:append = " bash"

SRC_URI:append = " \
    file://server.ttyS3.conf \
    "

OBMC_CONSOLE_TTYS:append = " ttyS3"

