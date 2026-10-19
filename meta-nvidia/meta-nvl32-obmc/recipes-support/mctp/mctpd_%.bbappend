FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://mctpd.conf \
    file://mctpd-net-raw.conf \
"

do_install:append() {
    install -d ${D}/etc/
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}/etc/mctpd.conf
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -m 0644 ${UNPACKDIR}/mctpd-net-raw.conf \
        ${D}${systemd_system_unitdir}/mctpd.service.d/mctpd-net-raw.conf
}

FILES:${PN} += " \
    /etc/mctpd.conf \
    ${systemd_system_unitdir}/mctpd.service.d/mctpd-net-raw.conf \
"
