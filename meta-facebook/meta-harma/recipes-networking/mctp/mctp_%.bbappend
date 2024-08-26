FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += " \
    file://setup-eid.conf \
    file://setup-local-eid \
    file://setup-bic-eid \
"

RDEPENDS:${PN} += " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d

    install -m 0644 ${WORKDIR}/setup-eid.conf \
              ${override_dir}/setup-eid.conf

    install -d ${D}${libexecdir}/mctp

    install -m 0755 ${WORKDIR}/setup-local-eid \
              ${D}${libexecdir}/mctp/
    install -m 0755 ${WORKDIR}/setup-bic-eid \
              ${D}${libexecdir}/mctp/
}
