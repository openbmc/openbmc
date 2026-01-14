FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


SRC_URI += " \
    file://setup-eid.conf \
    file://setup-local-eid \
    file://check-eid \
"

RDEPENDS:${PN} += " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d
    install -d ${D}${systemd_system_unitdir}/mctpd.service.d

    install -m 0644 ${UNPACKDIR}/setup-eid.conf \
              ${override_dir}/setup-eid.conf

    install -d ${D}${libexecdir}/mctp

    install -m 0755 ${UNPACKDIR}/setup-local-eid \
              ${D}${libexecdir}/mctp/
    install -m 0755 ${UNPACKDIR}/check-eid \
              ${D}${libexecdir}/mctp/
}
