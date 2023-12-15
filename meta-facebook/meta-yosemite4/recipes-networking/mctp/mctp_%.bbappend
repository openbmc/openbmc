FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://setup-local-eid.conf \
    file://setup-static-endpoints.conf \
    file://mctp-config.sh \
    file://setup-static-endpoints.sh \
    file://nic-gpio-addrs.sh \
"

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${datadir}/mctp
    install -m 0644 ${WORKDIR}/setup-local-eid.conf \
            ${override_dir}/setup-local-eid.conf
    install -m 0644 ${WORKDIR}/setup-static-endpoints.conf \
            ${override_dir}/setup-static-endpoints.conf
    install -m 0755 ${WORKDIR}/mctp-config.sh \
            ${D}${datadir}/mctp/
    install -m 0755 ${WORKDIR}/setup-static-endpoints.sh \
            ${D}${datadir}/mctp/
    install -d ${D}${datadir}/mctp
    install -m 0755 ${WORKDIR}/nic-gpio-addrs.sh \
            ${D}${datadir}/mctp/
}
