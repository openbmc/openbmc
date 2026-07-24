FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://mctpd.conf \
    file://mctp_init.service \
    file://mctp_init.sh \
    file://mctp_remove@.service \
    file://mctp_setup@.service \
    file://mctp_setup.sh \
    file://rainier-mctp-i3c@.service \
    file://rainier-mctp-i3c.sh \
"

SYSTEMD_SERVICE:${PN}:append = " \
    mctp_init.service \
    mctp_remove@.service \
    mctp_setup@.service \
    rainier-mctp-i3c@0.service \
"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/rainier-mctp-i3c@.service \
"

RDEPENDS:${PN}:append = "bash"

do_install:append () {
    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/*.sh ${D}${libexecdir}/mctp

    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}${sysconfdir}/mctpd.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}
}
