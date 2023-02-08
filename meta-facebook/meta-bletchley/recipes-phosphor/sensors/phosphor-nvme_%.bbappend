FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append:bletchley = " \
    file://nvme-json-rewrite \
    file://nvme-json-rewrite.conf \
"

RDEPENDS:${PN}:bletchley += "bash"

do_install:append:bletchley() {
        install -d ${D}${libexecdir}/${PN}/
        install -m 0755 ${WORKDIR}/nvme-json-rewrite ${D}${libexecdir}/${PN}/
}

SYSTEMD_OVERRIDE:${PN}:append:bletchley = " \
    nvme-json-rewrite.conf:xyz.openbmc_project.nvme.manager.service.d/nvme-json-rewrite.conf \
"
