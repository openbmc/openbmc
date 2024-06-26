FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://nvme_config.json"

do_install:append() {
    install -d ${D}${sysconfdir}/nvme
    install -m 0644 -D ${UNPACKDIR}/nvme_config.json ${D}/${sysconfdir}/nvme
}
