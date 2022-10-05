FILESEXTRAPATHS:prepend:scm-npcm845:= "${THISDIR}/files:"

SRC_URI:append:scm-npcm845= " \
    file://0001-networkd-create-new-socket.patch \
    file://systemd-networkd-wait-online.service \
"

SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " systemd-networkd-wait-online.service"

do_install:append:scm-npcm845() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/systemd-networkd-wait-online.service \
        ${D}${systemd_unitdir}/system

}
