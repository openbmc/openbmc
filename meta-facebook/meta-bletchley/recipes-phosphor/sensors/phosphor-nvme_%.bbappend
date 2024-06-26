FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://nvme-json-rewrite \
    file://nvme-json-rewrite.conf \
    file://nvme_config.json \
"

RDEPENDS:${PN} += "bash"

do_install:append() {
        install -d ${D}${libexecdir}/${PN}/
        install -m 0755 ${UNPACKDIR}/nvme-json-rewrite ${D}${libexecdir}/${PN}/

        install -d ${D}${sysconfdir}/nvme
        install -m 0644 -D ${UNPACKDIR}/nvme_config.json ${D}${sysconfdir}/nvme
}

SYSTEMD_OVERRIDE:${PN}:append = " \
    nvme-json-rewrite.conf:xyz.openbmc_project.nvme.manager.service.d/nvme-json-rewrite.conf \
"
