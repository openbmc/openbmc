FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://setup-local-eid.conf \
    file://setup-local-eid \
"

RDEPENDS:${PN} += " bash"
FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append () {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d

    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -m 0644 ${UNPACKDIR}/setup-local-eid.conf ${override_dir}/setup-local-eid.conf

    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/setup-local-eid ${D}${libexecdir}/mctp/
}
