FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
RDEPENDS:${PN}:append = " bash"

SRC_URI:append = " \
    file://server.ttyS3.conf \
    "

OBMC_CONSOLE_TTYS:append = " ttyS3"

