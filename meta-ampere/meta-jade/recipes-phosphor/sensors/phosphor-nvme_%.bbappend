FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://nvme_config.json"

do_install:append() {
    install -m 0644 -D ${WORKDIR}/nvme_config.json \
                   ${D}/etc/nvme
}
