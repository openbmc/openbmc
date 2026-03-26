FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " sync-mac"

EXTRA_OEMESON = "-Dforce-sync-mac=true"

SRC_URI += " \
    file://config.json \
    "
FILES:${PN} += "${datadir}/network/*.json"

do_install:append() {
    install -d ${D}${datadir}/network/
    install -m 0644 ${UNPACKDIR}/config.json ${D}${datadir}/network/
}

RDEPENDS:${PN}:append:bletchley15 = " bash"

SRC_URI:append:bletchley15 = " \
    file://wait-baseboard-inventory \
    file://wait-inventory.conf \
    "

FILES:${PN}:append:bletchley15 = " \
    ${libexecdir}/phosphor-network/wait-baseboard-inventory \
    ${systemd_system_unitdir}/xyz.openbmc_project.Network.service.d/wait-inventory.conf \
    "

do_install:append:bletchley15() {
    install -d ${D}${libexecdir}/phosphor-network
    install -m 0755 ${UNPACKDIR}/wait-baseboard-inventory ${D}${libexecdir}/phosphor-network/

    install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.Network.service.d
    install -m 0644 ${UNPACKDIR}/wait-inventory.conf \
        ${D}${systemd_system_unitdir}/xyz.openbmc_project.Network.service.d/wait-inventory.conf
}
