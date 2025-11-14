FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://wait-for-sma.conf \
"

FILES:${PN} += "${systemd_system_unitdir}/*"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/pldmd.service.d
    install -m 0644 ${UNPACKDIR}/wait-for-sma.conf \
      ${D}${systemd_system_unitdir}/pldmd.service.d/wait-for-sma.conf
}
