FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"

SRC_URI_append = " file://nvme_config.json"

do_install_append() {
    install -m 0644 -D ${WORKDIR}/nvme_config.json \
                   ${D}/etc/nvme
}
