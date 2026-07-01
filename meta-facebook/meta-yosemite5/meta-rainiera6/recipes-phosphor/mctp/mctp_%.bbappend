FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://mctpd.conf \
    file://mctp_init.sh \
    file://rainier-mctp-i3c@.service \
    file://rainier-mctp-i3c.sh \
"

SYSTEMD_SERVICE:${PN}:append = " \
    mctp_init.service \
    mctp_setup@.service \
    mctp_remove@.service \
    rainier-mctp-i3c@0.service \
"

RDEPENDS:${PN}:append = " bash"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/rainier-mctp-i3c@.service \
"

do_install:append () {
    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/*.sh ${D}${libexecdir}/mctp

    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}${sysconfdir}/mctpd.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}
}
