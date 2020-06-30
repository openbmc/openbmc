FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_gbs = " file://nvme_config.json"
SRC_URI_append_gbs = " file://nvme_json_rewrite.sh"
SRC_URI_append_gbs = " file://xyz.openbmc_project.nvme.manager.service.replace"

RDEPENDS_${PN} += "bash"

do_install_append_gbs() {
        install -d ${D}/${sysconfdir}/nvme/
        install -m 0644 ${WORKDIR}/nvme_config.json ${D}/${sysconfdir}/nvme/

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/nvme_json_rewrite.sh ${D}${bindir}/

        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/xyz.openbmc_project.nvme.manager.service.replace ${D}${systemd_system_unitdir}/xyz.openbmc_project.nvme.manager.service
}
