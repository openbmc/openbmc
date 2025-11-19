FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://mctp_init.service \
    file://mctp_init.sh \
    file://mctp_remove.service \
    file://mctp_setup.service \
    file://mctp_setup@.service \
    file://mctp_setup.sh \
"

SYSTEMD_SERVICE:${PN}:append = " \
    mctp_init.service \
    mctp_setup.service \
    mctp_remove.service \
"

RDEPENDS:${PN}:append = "bash"

do_install:append () {
    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/*.sh ${D}${libexecdir}/mctp

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}
}
