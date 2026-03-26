FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://nvme-json-rewrite \
    file://nvme-json-rewrite.conf \
    file://nvme_config.json \
"

RDEPENDS:${PN} += "bash"

FILES:${PN} += "${systemd_system_unitdir}/xyz.openbmc_project.nvme.manager.service.d/nvme-json-rewrite.conf"

do_install:append() {
        install -d ${D}${libexecdir}/${PN}/
        install -m 0755 ${UNPACKDIR}/nvme-json-rewrite ${D}${libexecdir}/${PN}/

        install -d ${D}${sysconfdir}/nvme
        install -m 0644 -D ${UNPACKDIR}/nvme_config.json ${D}${sysconfdir}/nvme

        install -d ${D}${systemd_system_unitdir}/xyz.openbmc_project.nvme.manager.service.d
        install -m 0644 ${UNPACKDIR}/nvme-json-rewrite.conf \
            ${D}${systemd_system_unitdir}/xyz.openbmc_project.nvme.manager.service.d/nvme-json-rewrite.conf
}
