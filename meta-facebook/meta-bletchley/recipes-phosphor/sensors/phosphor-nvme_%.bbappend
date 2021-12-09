FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://nvme_config.json"

do_install:append() {
        install -d ${D}/${sysconfdir}/nvme/
        install -m 0644 ${WORKDIR}/nvme_config.json ${D}/${sysconfdir}/nvme/
}
