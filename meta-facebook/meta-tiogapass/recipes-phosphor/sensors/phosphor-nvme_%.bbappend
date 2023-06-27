FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:tiogapass = " file://nvme_config.json"

do_install:append:tiogapass() {
    install -d ${D}${sysconfdir}/nvme
    install -m 0644 -D ${WORKDIR}/nvme_config.json ${D}/${sysconfdir}/nvme
}
