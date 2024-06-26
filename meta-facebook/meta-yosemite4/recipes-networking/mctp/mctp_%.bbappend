FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://setup-local-eid.conf \
    file://setup-static-endpoints.conf \
    file://mctp-config \
    file://setup-static-endpoints \
    file://nic-gpio-addrs \
"

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d

    install -m 0644 ${UNPACKDIR}/setup-local-eid.conf \
            ${override_dir}/setup-local-eid.conf
    install -m 0644 ${UNPACKDIR}/setup-static-endpoints.conf \
            ${override_dir}/setup-static-endpoints.conf

    install -d ${D}${libexecdir}/mctp

    install -m 0755 ${UNPACKDIR}/mctp-config \
            ${D}${libexecdir}/mctp/
    install -m 0755 ${UNPACKDIR}/setup-static-endpoints \
            ${D}${libexecdir}/mctp/
    install -m 0755 ${UNPACKDIR}/nic-gpio-addrs \
            ${D}${libexecdir}/mctp/
}
