FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:gbs = " file://nvme_config.json"
SRC_URI:append:gbs = " file://nvme_json_rewrite.sh"
SRC_URI:append:gbs = " file://xyz.openbmc_project.nvme.manager.service.replace"

RDEPENDS:${PN} += "bash"

do_install:append:gbs() {
        install -d ${D}/${sysconfdir}/nvme/
        install -m 0644 ${WORKDIR}/nvme_config.json ${D}/${sysconfdir}/nvme/

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/nvme_json_rewrite.sh ${D}${bindir}/

        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/xyz.openbmc_project.nvme.manager.service.replace ${D}${systemd_system_unitdir}/xyz.openbmc_project.nvme.manager.service
}
