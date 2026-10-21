FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit systemd

SRC_URI:append = " \
  file://mctp-local.service \
  file://mctpd.conf \
"

SYSTEMD_SERVICE:${PN} += "mctp-local.service"


FILES:${PN}:append = " \
    ${sysconfdir}/mctpd.conf \
"

do_install:append() {
    install -m 0644 ${UNPACKDIR}/mctp-local.service ${D}${systemd_system_unitdir}/

    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}${sysconfdir}/mctpd.conf
}

