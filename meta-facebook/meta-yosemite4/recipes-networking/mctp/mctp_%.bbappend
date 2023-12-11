FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += " \
    file://setup-local-eid.conf \
    file://mctp-config.sh \
"

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${datadir}/mctp
    install -m 0644 ${WORKDIR}/setup-local-eid.conf \
            ${override_dir}/setup-local-eid.conf
    install -m 0755 ${WORKDIR}/mctp-config.sh \
            ${D}${datadir}/mctp/
}
