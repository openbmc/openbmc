FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://uart-routing.sh \
                   file://use-socket.conf.in \
                 "

RDEPENDS:${PN}:append = " bash"

OBMC_CONSOLE_HOST_TTY:ncplite = "ttyS3"

do_install:append() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/uart-routing.sh -D ${D}${sbindir}/uart-routing.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0755 ${UNPACKDIR}/use-socket.conf.in -D ${D}${systemd_system_unitdir}/obmc-console-ssh@.service.d/use-socket.conf
}
